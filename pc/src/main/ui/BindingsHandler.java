package main.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.cef.browser.CefBrowser;

import main.ui.ParamDTO.TypeNotSupportedException;

public class BindingsHandler {

    private final Class<?> clazz;
    private Object classInstance;

    public BindingsHandler(Class<?> clazzInterface) {
        this.clazz = clazzInterface;
        classInstance = null;
        try {
            classInstance = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
    }
    
    public ParamDTO callMethod(MethodDTO method){
        try {
            Method classMethod = clazz.getMethod(method.name, method.getParamsTypes());
            ParamDTO retPar = new ParamDTO(classMethod.getReturnType().getSimpleName());
            retPar.setData( classMethod.invoke(classInstance, method.getParams()));
            return retPar;
        } catch (IllegalAccessException | InvocationTargetException | TypeNotSupportedException |
                    NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
            return new ParamDTO("Error");
        }
    }

    public void injectJavascriptInterface(CefBrowser browser){
        StringBuilder sb = new StringBuilder();
        for(Method classMethod : clazz.getMethods()){
            JavascriptInterface annotation = classMethod.getAnnotation(JavascriptInterface.class);
            if(annotation == null) 
                continue;

            StringBuilder innerSb = new StringBuilder();


            innerSb.append("function parseReturn(data){" +
                                "const obj = JSON.parse(data);" +
                                "if(obj.type === \"int\" || obj.type === \"Integer\"){" +
                                    "return parseInt(obj.data);" +
                                "} else if(obj.type === \"float\" || obj.type === \"Float\"){" +
                                    "return parseFloat(obj.data);" +
                                "} else if(obj.type === \"boolean\" || obj.type === \"Boolean\"){" +
                                    "return obj.data === 'true';"+
                                "} else if(obj.type === \"void\"){" +
                                    "return;"+
                                "} else "+
                                    "return obj.data;"+
                            "}");
            innerSb.append( "function " + classMethod.getName() + "(");

            for(int i = 0; i < classMethod.getParameters().length; i++){
                innerSb.append( classMethod.getParameters()[i].getName() );
                if(i + 1 < classMethod.getParameters().length){
                    innerSb.append(",");
                }
            }

            innerSb.append(             "){" +
                                "return new Promise((resolve, reject) => {" +
                                    "cefQuery({" +
                                        "request: JSON.stringify({" +
                                            "name: \"" + classMethod.getName() + "\"," +
                                            "params: [");
            for(int i = 0; i < classMethod.getParameters().length; i++){
                innerSb.append("{ data: " + classMethod.getParameters()[i].getName() + "," +
                                "type: \"" + classMethod.getParameters()[i].getType().getSimpleName() + "\"}");
                if(i + 1 < classMethod.getParameters().length){
                    innerSb.append(",");
                }
            }
            innerSb.append(             "]})," +
                                        "onSuccess: (response) => resolve(parseReturn(response))," +
                                        "onFailure: (response) => reject(response)" +
                                    "})" +
                                "})" +
                            "}");
            sb.append(innerSb.toString());
        }
        browser.executeJavaScript(sb.toString(), browser.getURL(), 0);
        System.out.println(sb.toString());
    }

}
