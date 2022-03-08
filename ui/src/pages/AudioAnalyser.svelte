<script>
    try {
        navigator.mediaDevices.getUserMedia({audio:true}).then(function(localStream){
            const ctx = new(window.AudioContext || window.webkitAudioContext)()
            console.log(ctx.sampleRate)
            const audioSrc = ctx.createMediaStreamSource(localStream)
            const analyser = ctx.createAnalyser()

            audioSrc.connect(analyser)
            analyser.fftSize = 512
            const bufferLength = analyser.frequencyBinCount
            const frequencyData = new Uint8Array(bufferLength)

            setInterval(() => {
                analyser.getByteFrequencyData(frequencyData)
                //console.log(frequencyData);
                printFrequencyData(frequencyData)
            }, 1000)
        })
        .catch(function(){
            //Handle error
        })

        function printFrequencyData(frequencyData) {
            const windowLen = 10
            const offset = 209
            const dBLimit = 120
            let found = false
            for (let i = 0; i < windowLen; i++) {
                found = found || (frequencyData[i + offset] > dBLimit)
            }
            if(found)
                console.log("Found sound")
        }
    } catch(e) {
        console.log("Not using HTTPS, some features are not available")
    }
</script>