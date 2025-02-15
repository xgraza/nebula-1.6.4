/**
    Heavily modified from https://www.shadertoy.com/view/WtdSDs
*/

#version 120

uniform vec2 rectSize;
uniform vec4 color;
uniform float radius;
uniform float edgeSoftness;

float roundedBoxSDF(vec2 CenterPosition, vec2 size, float r) {
    return length(max(abs(CenterPosition) - size + r, 0.0)) - r;
}

void main() {
    if (rectSize == vec2(0.0, 0.0) || color.a == 0.0 || radius == 0.0) return;

    vec2 rectHalf       = rectSize * .5;
    float distance      = roundedBoxSDF(rectHalf - (gl_TexCoord[0].st * rectSize), rectHalf, radius);
    float smoothedAlpha = 1.0 - smoothstep(0.0, edgeSoftness * 2.0, distance);
    gl_FragColor        = vec4(color.rgb, color.a * smoothedAlpha);
}
