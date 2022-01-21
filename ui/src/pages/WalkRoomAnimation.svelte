<script>
    export let roomName = ""
    let animationId = 0
    let numCorners = 8
    let animationClaas = "animation-0"
    export function nextCorner(){
        animationId++
        if(animationId >= numCorners)
            animationId = 0
        animationClaas = "animation-" + animationId
    }

</script>
<svg
    xmlns="http://www.w3.org/2000/svg"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    version="1.1"
    width="500"
    height="500"
    viewBox="0 0 400 500"
    xml:space="preserve"
>
    <g transform="matrix(1.02 0 0 1 200 231)">
        <path
            style="stroke: rgb(0,0,0); stroke-width: 6; stroke-dasharray: none; stroke-linecap: butt; stroke-dashoffset: 0; stroke-linejoin: miter; stroke-miterlimit: 4; fill: none; fill-rule: nonzero; opacity: 1;"
            vector-effect="non-scaling-stroke"
            transform=" translate(-154.5, -148)"
            d="M 0 0 L 219 0 L 219 84 L 309 84 L 309 296 L 97 296 L 97 219 L 0 219 z"
            stroke-linecap="round"
        />
    </g>
    <g
        xmlns="http://www.w3.org/2000/svg"
        transform="matrix(1 0 0 0.97 200 230)"
    >
        <path
            id ="path"
            class = {animationClaas}
            xmlns="http://www.w3.org/2000/svg"
            style="stroke: rgb(98,0,238); stroke-width: 24; stroke-dasharray: none; stroke-linecap: butt; stroke-dashoffset: 0; stroke-linejoin: miter; stroke-miterlimit: 4; fill: none; fill-rule: nonzero; opacity: 1;"
            vector-effect="non-scaling-stroke"
            transform=" translate(-132, -124)"
            d="M 0 0 L 171 0 L 171 82 L 264 82 L 264 248 L 93 248 L 93 171 L 0 171 z"
            stroke-linecap="round"
        />
    </g>
    <g
        xmlns="http://www.w3.org/2000/svg"
        transform="matrix(1 0 0 0.99 212.5 230)"
    >
        <text
            xml:space="preserve"
            font-family="Roboto"
            font-size="40"
            font-style="normal"
            font-weight="700"
            line-height="1.16"
            style="stroke: none; stroke-width: 1; stroke-dasharray: none; stroke-linecap: butt; stroke-dashoffset: 0; stroke-linejoin: miter; stroke-miterlimit: 4; fill: rgb(0,0,0); fill-rule: nonzero; opacity: 1; white-space: pre;"
            ><tspan x="-119.12" y="13.51">{roomName}</tspan></text
        >
    </g>
</svg>

<style>


    svg {
        width: 100%;
    }

    #path {
        stroke-dasharray: 0 1000 !important;
        stroke-linecap: round !important;
        animation-fill-mode: forwards;

        --c0-pos: 157;
        --c1-pos: 248;
        --c2-pos: 324;
        --c3-pos: 495;
        --c4-pos: 656;
        --c5-pos: 750;
        --c6-pos: 831;
        --c7-pos: 990;
    }
    .animation-0 {
        animation: dash-0 1s linear;
    }
    .animation-1 {
        animation: dash-1 0.5s linear;
    }
    .animation-2 {
        animation: dash-2 0.5s linear;
    }
    .animation-3 {
        animation: dash-3 1s linear;
    }
    .animation-4 {
        animation: dash-4 1s linear;
    }
    .animation-5 {
        animation: dash-5 0.5s linear;
    }
    .animation-6 {
        animation: dash-6 0.5s linear;
    }
    .animation-7 {
        animation: dash-7 1s linear;
    }

    /*@keyframes dash {
        to {
            stroke-dashoffset: 1000;
        }
    }*/

    @keyframes dash-0 {
        from {
            stroke-dashoffset: 0;
        }
        to {
            stroke-dashoffset: var(--c0-pos);
        }
    }
    @keyframes dash-1 {
        from {
            stroke-dashoffset: var(--c0-pos);
        }
        to {
            stroke-dashoffset: var(--c1-pos);
        }
    }
    @keyframes dash-2 {
        from {
            stroke-dashoffset: var(--c1-pos);
        }
        to {
            stroke-dashoffset: var(--c2-pos);
        }
    }
    @keyframes dash-3 {
        from {
            stroke-dashoffset: var(--c2-pos);
        }
        to {
            stroke-dashoffset: var(--c3-pos);
        }
    }
    @keyframes dash-4 {
        from {
            stroke-dashoffset: var(--c3-pos);
        }
        to {
            stroke-dashoffset: var(--c4-pos);
        }
    }
    @keyframes dash-5 {
        from {
            stroke-dashoffset: var(--c4-pos);
        }
        to {
            stroke-dashoffset: var(--c5-pos);
        }
    }
    @keyframes dash-6 {
        from {
            stroke-dashoffset: var(--c5-pos);
        }
        to {
            stroke-dashoffset: var(--c6-pos);
        }
    }
    @keyframes dash-7 {
        from {
            stroke-dashoffset: var(--c6-pos);
        }
        to {
            stroke-dashoffset: var(--c7-pos);
        }
    }
</style>
