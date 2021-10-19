cp -r ./pc/src/it/unibo/sca/multiroomaudio/shared ./android/app/src/main/java/it/unibo/sca/multiroomaudio
sed -i 's/it.unibo.sca.multiroomaudio.ui.JavascriptInterface/android.webkit.JavascriptInterface/g' ./android/app/src/main/java/it/unibo/sca/multiroomaudio/shared/JavascriptBindings.java
