<script>
    import { Page, Navbar, Block } from 'framework7-svelte';
    import { onMount } from 'svelte';
    import { f7ready } from 'framework7-svelte';
   
    const urlParams = new URLSearchParams(window.location.search);
    let clientId = urlParams.get('clientId');
	let socket;
    let type = urlParams.get('type');

    onMount(() => {
        f7ready(() => {
            socketSetup()
        })
    })

    function socketSetup(){
        socket = new WebSocket("ws://" + location.hostname + "/websocket")

        socket.onopen = () => {
            sendInitMessage()
        }

        socket.onmessage = (event) => {
            processMessage(JSON.parse(event.data))
        }

        socket.onclose = (event) => {
            console.log(event)
        }
    }

    function sendInitMessage(){
      console.log(clientId);
        socket.send(JSON.stringify({
            type : "HELLO",
            deviceType : 0, // speaker type
            id: clientId
        }))
    }

	function processMessage(message) {
        console.log(message)
        switch(message.type){
            case "REJECTED":
                socket.close("Connection rejected");
                break;
            case "HELLO_BACK":
                console.log("Connected");
                window.location.replace("http://" + location.hostname + "/?type=" + type)

                
                break;
			default: 
				console.log("unrecognized: " + message);
				break;
        }    
	}
</script>

<Page>
    <!-- Top Navbar -->
    <Navbar title="About" backLink="Back"></Navbar>

    <Block strong>
        <p>Sorry</p>
        <p>BELLA.</p>
    </Block>
</Page>