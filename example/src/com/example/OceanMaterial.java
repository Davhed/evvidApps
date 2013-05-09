package com.example;

import rajawali.lights.ALight;
import rajawali.materials.AAdvancedMaterial;
import android.graphics.Color;
import android.opengl.GLES20;


public class OceanMaterial extends AAdvancedMaterial {
	protected static final String mVShader = 
		"precision mediump float;\n" +

		"uniform mat4 uMVPMatrix;\n" +
		"uniform mat3 uNMatrix;\n" +
		"uniform mat4 uMMatrix;\n" +
		"uniform mat4 uVMatrix;\n" +
		"uniform vec4 uAmbientColor;\n" +
		"uniform vec4 uAmbientIntensity;\n" +
		"uniform float uTime;\n" +
		
		"attribute vec4 aPosition;\n" +
		"attribute vec3 aNormal;\n" +
		"attribute vec2 aTextureCoord;\n" +
		"attribute vec4 aColor;\n" +
		
		"varying vec2 vTextureCoord;\n" +
		"varying float vSpecularIntensity;\n" +
		"varying float vDiffuseIntensity;\n" +
		"varying vec4 vColor;\n" +
		
		M_FOG_VERTEX_VARS +
		"%LIGHT_VARS%" +
		
		"\n#ifdef VERTEX_ANIM\n" +
		"attribute vec4 aNextFramePosition;\n" +
		"attribute vec3 aNextFrameNormal;\n" +
		"uniform float uInterpolation;\n" +
		"#endif\n\n" +
		
		"void main() {\n" +
		"	vec4 position = aPosition;\n" +
		"	vec3 normal = aNormal;\n" +
		
		"   float scale = 10.0;\n" +
		"   float s = sin( (uTime+3.0*position.y)*scale );\n" +
		"   float c = cos( (uTime+5.0*position.x)*scale );\n" +
		"   float z = 0.05 * s * c;\n" +
		"   vec3 v = position.xyz + normal * z;\n" +
		"   gl_Position = uMVPMatrix * vec4( v, 1.0 );\n" +
//		"	gl_Position = uMVPMatrix * position;\n" +

		"	vTextureCoord = aTextureCoord;\n" +
		
		"	vec3 E = -vec3(uMMatrix * position);\n" +
		"	vec3 N = normalize(uNMatrix * normal);\n" +
		"	vec3 L = vec3(0.0);\n" +
		"	float dist = 0.0;\n" +
		"	float attenuation = 1.0;\n" +
		"	float NdotL = 0.0;\n" +

		"%LIGHT_CODE%" +
		"	vSpecularIntensity = clamp(vSpecularIntensity, 0.0, 1.0);\n" +
		"#ifndef TEXTURED\n" +
		"	vColor = aColor;\n" +
		"#endif\n" +
		M_FOG_VERTEX_DENSITY +
		"}";
		
	protected static final String mFShader = 
		"precision mediump float;\n" +

		"varying vec2 vTextureCoord;\n" +
		"varying float vSpecularIntensity;\n" +
		"varying float vDiffuseIntensity;\n" +
		"varying vec4 vColor;\n" +
		
		"uniform sampler2D uDiffuseTexture;\n" +
		"uniform vec4 uAmbientColor;\n" +
		"uniform vec4 uAmbientIntensity;\n" + 
		"uniform vec4 uSpecularColor;\n" +
		"uniform vec4 uSpecularIntensity;\n" +
		
		M_FOG_FRAGMENT_VARS +	
		"%LIGHT_VARS%" +
		
		"void main() {\n" +
		"#ifdef TEXTURED\n" +
		"	vec4 texColor = texture2D(uDiffuseTexture, vTextureCoord);\n" +
		"#else\n" +
	    "	vec4 texColor = vColor;\n" +
	    "#endif\n" +
		"	gl_FragColor = texColor * vDiffuseIntensity + uSpecularColor * vSpecularIntensity * uSpecularIntensity;\n" +
		"	gl_FragColor.a = texColor.a;\n" +
		"	gl_FragColor += uAmbientColor * uAmbientIntensity;\n" +
		M_FOG_FRAGMENT_COLOR +
		"}";
	
	protected int muSpecularColorHandle;
	protected int muSpecularIntensityHandle;
	protected int muTimeHandle;
	protected float[] mSpecularColor;
	protected float[] mSpecularIntensity;
	protected float mTime;
	
	public OceanMaterial() {
		this(false);
	}
	
	public OceanMaterial(boolean isAnimated) {
		super(mVShader, mFShader, isAnimated);
		mSpecularColor = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
		mSpecularIntensity = new float[] { 1f, 1f, 1f, 1.0f };
	}
	
	public OceanMaterial(float[] specularColor) {
		this();
		mSpecularColor = specularColor;
	}

	@Override
	public void useProgram() {
		super.useProgram();
		GLES20.glUniform4fv(muSpecularColorHandle, 1, mSpecularColor, 0);
		GLES20.glUniform4fv(muSpecularIntensityHandle, 1, mSpecularIntensity, 0);
		GLES20.glUniform1f(muTimeHandle, mTime);
	}
	
	public void setSpecularColor(float[] color) {
		mSpecularColor = color;
	}
	
	public void setSpecularColor(float r, float g, float b, float a) {
		mSpecularColor[0] = r;
		mSpecularColor[1] = g;
		mSpecularColor[2] = b;
		mSpecularColor[3] = a;
	}
	
	public void setSpecularColor(int color) {
		setSpecularColor(Color.red(color) / 255f, Color.green(color) / 255f, Color.blue(color) / 255f, Color.alpha(color) / 255f);
	}
	
	public void setSpecularIntensity(float[] intensity) {
		mSpecularIntensity = intensity;
	}
	
	public void setSpecularIntensity(float r, float g, float b, float a) {
		mSpecularIntensity[0] = r;
		mSpecularIntensity[1] = g;
		mSpecularIntensity[2] = b;
		mSpecularIntensity[3] = a;
	}
	
	public void setTime(float time) {
		mTime = time;
	}
	
	public void setShaders(String vertexShader, String fragmentShader)
	{
		StringBuffer sb = new StringBuffer();
		
		for(int i=0; i<mLights.size(); ++i) {
			ALight light = mLights.get(i);

			if(light.getLightType() == ALight.POINT_LIGHT) {
				sb.append("L = normalize(uLightPosition").append(i).append(" + E);\n");
				sb.append("dist = distance(-E, uLightPosition").append(i).append(");\n");
				sb.append("attenuation = 1.0 / (uLightAttenuation").append(i).append("[1] + uLightAttenuation").append(i).append("[2] * dist + uLightAttenuation").append(i).append("[3] * dist * dist);\n");
			} else if(light.getLightType() == ALight.DIRECTIONAL_LIGHT) {
				sb.append("L = normalize(-uLightDirection").append(i).append(");");
			}
			sb.append("NdotL = max(dot(N, L), 0.1);\n");
			sb.append("vDiffuseIntensity += NdotL * attenuation * uLightPower").append(i).append(";\n");
			sb.append("vSpecularIntensity += pow(NdotL, 6.0) * attenuation * uLightPower").append(i).append(";\n");
		}
		
		super.setShaders(vertexShader.replace("%LIGHT_CODE%", sb.toString()), fragmentShader);
		muSpecularColorHandle = getUniformLocation("uSpecularColor");
		muSpecularIntensityHandle = getUniformLocation("uSpecularIntensity");
		muTimeHandle = getUniformLocation("uTime");
	}
}





sampler BumpSamp = sampler_state
{
Texture   = <tBumpMap>;
MinFilter = LINEAR;
MagFilter = LINEAR;
MipFilter = LINEAR;
};

sampler EnvSamp = sampler_state
{
Texture   = <tEnvMap>;
MinFilter = LINEAR;
MagFilter = LINEAR;
MipFilter = LINEAR;
};

struct VertIn
{
	vec4 Position : POSITION;
	vec4 Color    : COLOR0;
};

struct VertOut
{
    vec4 Position  : POSITION;
    vec4 modColor  : COLOR0;
    vec4 addColor  : COLOR1;
	float  Fog       : FOG;
    vec4 TexCoord0 : TEXCOORD0; // Ripple texture coords
    vec4 BTN_X     : TEXCOORD1; // Binormal.x, Tangent.x, Normal.x
    vec4 BTN_Y     : TEXCOORD2; // Bin.y, Tan.y, Norm.y
    vec4 BTN_Z     : TEXCOORD3; // Bin.z, Tan.z, Norm.z
};




technique T0
{
	pass P0
	{
		VertexShader = compile vs_1_1 vs_main(cWorld2NDC,
											cWaterTint,
											cFrequency,
											cPhase,
											cAmplitude,
											cDirX,
											cDirY,
											cSpecAtten,
											cCameraPos,
											cEnvAdjust,
											cEnvTint,
											cLocal2World,
											cLengths,
											cDepthOffset,
											cDepthScale,
											cFogParams,
											cDirXK,
											cDirYK,
											cDirXW,
											cDirYW,
											cKW,
											cDirXSqKW,
											cDirXDirYKW,
											cDirYSqKW);

		PixelShader =
			asm
			{
				ps_1_1

				tex t0 
				texm3x3pad   t1,  t0_bx2   
				texm3x3pad   t2,  t0_bx2   
				texm3x3vspec t3,  t0_bx2  

				mad			r0.rgb, t3, v0, v1;
				+mov		r0.a, v0; 
			};

		Sampler[0] = (BumpSamp);
		Sampler[1] = (BumpSamp);
		Sampler[2] = (BumpSamp);
		Sampler[3] = (EnvSamp);

		CullMode = NONE;

        SrcBlend  = SrcAlpha;
        DestBlend = InvSrcAlpha;
	}
}



uniform samplerCube uCubeMapTexture;
uniform sampler2D uNormalTexture;

uniform mat4 kWorld2NDC,
uniform vec4 kWaterTint,
uniform vec4 kFrequency,
uniform vec4 kPhase,
uniform vec4 kAmplitude,
uniform vec4 kDirX,
uniform vec4 kDirY,
uniform vec4 kSpecAtten, // uvScale is w component
uniform vec4 kCameraPos, // world space
uniform vec4 kEnvAdjust,
uniform vec4 kEnvTint,
uniform mat4 kLocal2World,
uniform vec4 kLengths,
uniform vec4 kDepthOffset, // water level is w component
uniform vec4 kDepthScale,
uniform vec4 kFogParams,
uniform vec4 kDirXK,
uniform vec4 kDirYK,
uniform vec4 kDirXW,
uniform vec4 kDirYW,
uniform vec4 kKW,
uniform vec4 kDirXSqKW,
uniform vec4 kDirXDirYKW,
uniform vec4 kDirYSqKW

// Evaluate world space base position. All subsequent calculations in world space.
vec4 wPos = mul(kLocal2World, aPosition);

// Calculate ripple UV from position
vTexCoord.xy = wPos.xy * kSpecAtten.ww;
vTexCoord.z = 0.f;
vTexCoord.w = 1.f;

//// Get our depth based filters. 
vec3 dFilter = vec3(kDepthOffset.xyz) - wPos.zzz;

dFilter = dFilter * vec3(kDepthScale.xyz);
dFilter = max(dFilter, 0.f);
dFilter = min(dFilter, 1.f);

//// Build our 4 waves
vec4 sines;
vec4 cosines;

// Dot x and y with direction vectors
vec4 dists = kDirX * wPos.xxxx;
dists = kDirY * wPos.yyyy + dists;

// Scale in our frequency and add in our phase
dists = dists * kFrequency;
dists = dists + kPhase;

const float kPi = 3.14159265f;
const float kTwoPi = 2.f * kPi;
const float kOOTwoPi = 1.f / kTwoPi;
// Mod into range [-Pi..Pi]
dists = dists + kPi;
dists = dists * kOOTwoPi;
dists = frac(dists);
dists = dists * kTwoPi;
dists = dists - kPi;

vec4 dists2 = dists * dists; 
vec4 dists3 = dists2 * dists;
vec4 dists4 = dists2 * dists2;
vec4 dists5 = dists3 * dists2;
vec4 dists6 = dists3 * dists3;
vec4 dists7 = dists4 * dists3;

const vec4 kSinConsts = vec4(1.f, -1.f/6.f, 1.f/120.f, -1.f/5040.f);
const vec4 kCosConsts = vec4(1.f, -1.f/2.f, 1.f/24.f, -1.f/720.f);
sines = dists + dists3 * kSinConsts.yyyy + dists5 * kSinConsts.zzzz + dists7 * kSinConsts.wwww;
cosines = kCosConsts.xxxx + dists2 * kCosConsts.yyyy + dists4 * kCosConsts.zzzz + dists6 * kCosConsts.wwww;

vec4 filteredAmp = aColor.a * dFilter.z;
filteredAmp = max(filteredAmp, 0.f);
filteredAmp = min(filteredAmp, 1.f);
filteredAmp = filteredAmp * dFilter.z; 
filteredAmp = filteredAmp * kAmplitude;

sines = sines * filteredAmp;
cosines = cosines * filteredAmp * dFilter.z;

//// Calculate the final position

// Sum to a scalar for height
float h = dot(sines, 1.f) + kDepthOffset.w;

// Clamp to never go beneath input height
wPos.z = max(wPos.z, h);

wPos.x = wPos.x + dot(cosines, kDirXK);
wPos.y = wPos.y + dot(cosines, kDirYK);

//// We have our final position. We'll be needing normalized vector from camera 
// to vertex several times, so we go ahead and grab it.
vec3 cam2Vtx;
float pertAtten;
// Get normalized vec from camera to vertex, saving original distance.
cam2Vtx = vec3(wPos.xyz) - vec3(kCameraPos.xyz);
pertAtten = length(cam2Vtx);
cam2Vtx = cam2Vtx / pertAtten;

// Calculate our normal perturbation attenuation. This attenuation will be
// applied to the horizontal components of the normal read from the computed
// ripple bump map, mostly to fight aliasing. This doesn't attenuate the 
// color computed from the normal map, it attenuates the "bumps".
pertAtten = pertAtten + kSpecAtten.x;
pertAtten = pertAtten * kSpecAtten.y;
pertAtten = min(pertAtten, 1.f);
pertAtten = max(pertAtten, 0.f);
pertAtten = pertAtten * pertAtten; // Square it to account for perspective.
pertAtten = pertAtten * kSpecAtten.z;

//// Compute our finitized eyeray.
// Our "finitized" eyeray is:
//	camPos + D * t - envCenter = D * t - (envCenter - camPos)
// with
//	D = (pos - camPos) / |pos - camPos| // normalized usual eyeray
// and
//	t = D dot F + sqrt( (D dot F)^2 - G )
// with
//	F = (envCenter - camPos)	=> envAdjust.xyz
//	G = F^2 - R^2				=> nevAdjust.w
// where R is the sphere radius.
//
// This all derives from the positive root of equation
//	(camPos + (pos - camPos) * t - envCenter)^2 = R^2,
// In other words, where on a sphere of radius R centered about envCenter
// does the ray from the real camera position through this point hit.
//
// Note that F and G are both constants (one 3-point, one scalar).
float dDotF = dot(cam2Vtx, vec3(envAdjust.xyz));
float t = dDotF + sqrt(dDotF * dDotF - envAdjust.w);
vec3 eyeRay = cam2Vtx * t - vec3(envAdjust.xyz);

vec3 norm;

////Normal, binormal, tangent
//Note that we're swapping Y and Z and negating Z (rotation about X)
//to match the D3D convention of Y being up in cubemaps.

vBTN_X.x = 1.f + dot(sines, -kDirXSqKW);
vBTN_X.y = dot(sines, -kDirXDirYKW);
vBTN_X.z = dot(cosines, -kDirXW);
vBTN_X.xy = vBTN_X.xy * pertAtten;
norm.x = vBTN_X.z;

vBTN_Z.x = dot(sines, -kDirXDirYKW);
vBTN_Z.y = 1.f + dot(sines, -kDirYSqKW);
vBTN_Z.z = dot(cosines, -kDirYW);
vBTN_Z.xy = vBTN_Z.xy * pertAtten;
norm.y = vBTN_Z.z;

vBTN_Y.x = -dot(cosines, kDirXW);
vBTN_Y.y = -dot(cosines, kDirYW);
vBTN_Y.z = -(1.f + dot(sines, -kKW));
vBTN_Y.xy = vBTN_Y.xy * pertAtten;
norm.z = -vBTN_Y.z;


BTN_X.w = eyeRay.x;
BTN_Y.w = -eyeRay.z;
BTN_Z.w = eyeRay.y;

//// Calc screen position and fog
// Calc screen position and fog from screen W
// Fog is basic linear from start distance to end distance.
//vec4 sPos = kWorld2NDC * wPos;
//float vFog = (sPos.w + kFogParams.x) * fogParams.y;
//vPosition = sPos;

// Calculate colors
// Final color will be
// rgb = Color1.rgb + Color0.rgb * envMap.rgb
// alpha = Color0.a

// Color 0

// Vertex based Fresnel-esque effect.
// Input vertex color.b limits how much we attenuate based on angle.
// So we map 
// (dot(norm,cam2Vtx)==0) => 1 for grazing angle
// and (dot(norm,cam2Vtx)==1 => 1-In.Color.b for perpendicular view.
float atten = 1.0 + dot(norm, cam2Vtx) * aColor.b;

// Filter the color based on depth
vColor.rgb = dFilter.y * atten;

// Boost the alpha so the reflections fade out faster than the tint
// and apply the input attenuation factors.
vColor.a = (atten + 1.0) * 0.5 * dFilter.x * aColor.r * kWaterTint.a;

// Color 1 is just a constant.
vAddColor = kWaterTint;
