<script>
    export let roomName = ""
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
            class="path"
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
        style=""
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
    .path {
        stroke-dasharray: 0 1000 !important;
        stroke-linecap: round !important;
        animation: dash 7s linear infinite;
    }

    @keyframes dash {
        to {
            stroke-dashoffset: 1000;
        }
    }
    svg {
        width: 100%;
    }
</style>
