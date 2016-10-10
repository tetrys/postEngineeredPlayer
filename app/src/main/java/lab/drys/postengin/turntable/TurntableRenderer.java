package lab.drys.postengin.turntable;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.support.v4.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import lab.drys.postengin.MainActivityReceiver;
import lab.drys.postengin.PostEngin;
import lab.drys.postengin.R;

/**
 * Created by lykanthrop on 1507/19/.
 */
public class TurntableRenderer implements GLSurfaceView.Renderer
{
	public TurntableRenderer(Context cnt)
	{
		context = cnt;

		firstDraw = true;
		vinylOn = false;

		glProgram = new int[3];
		textures = new int[6];

		projectionMatrixHandle = new int[2];
		textureHandle = new int[3];
		transMatrixHandle = new int[2];
		transVectorHandle = new int[1];
		transYHandle = new int[2];

		projectionMatrix = new float[4];

		discVecBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
		armVecBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
		texVecBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
		deckUVecBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
		deckMVecBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
		deckDVecBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vinylVecBuffer = ByteBuffer.allocateDirect(32).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	public void set(Turntable tbl)
	{
		this.firstDraw = true;

		if(turntable==null)
		{
			turntable = tbl;
			this.initTurntable();
		}
	}

	private void initTurntable()
	{
		turntable.width = width;
		turntable.height = height;
		turntable.axisX = width/2.0f;
		turntable.axisY = height/2.0f;
		turntable.armRatio = armRatio;
		turntable.setDiscVariables();
		turntable.setArmVariables();
	}

	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config)
	{
		PostEngin.logDebug(GLES20.glGetString(GLES20.GL_EXTENSIONS));
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

		this.initGL();
		this.initProgram();
	}

	@Override
	public void onSurfaceChanged(GL10 unused, int w, int h)
	{
		width = w;
		height = h;
		GLES20.glViewport(0, 0, w, h);

		projectionMatrix[0] = 2.0f/w;
		projectionMatrix[1] = 0.0f;
		projectionMatrix[2] = 0.0f;
		projectionMatrix[3] = 2.0f/h;

		this.initArmImage();
		this.initDeckImage();
		this.initGearImage();

		if(turntable.vinylOn)
		{
			if(turntable.vinylPath!=null)
			{
				this.loadVinyl(turntable.vinylPath);
			}
			else
			{
				this.loadVinyl(R.drawable.schallplatte);
			}
		}

		this.initBuffers();
		this.initProjectionMatrix();

		if(turntable!=null)
		{
			this.initTurntable();
		}
	}

	@Override
	public void onDrawFrame(GL10 unused)
	{
		//GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		this.draw();
	}

	private void initGL()
	{
		GLES20.glGenTextures(6, textures, 0);
	}

	private void initProjectionMatrix()
	{
		GLES20.glUseProgram(glProgram[0]);
		GLES20.glUniformMatrix2fv(projectionMatrixHandle[0], 1, false, projectionMatrix, 0);
		GLES20.glUseProgram(0);

		GLES20.glUseProgram(glProgram[1]);
		GLES20.glUniformMatrix2fv(projectionMatrixHandle[1], 1, false, projectionMatrix, 0);
		GLES20.glUseProgram(0);
	}

	private void initProgram()
	{
		String fragmentShaderCode = this.loadShaderCode(context.getResources().openRawResource(R.raw.fragment_shader));
		int fshd = this.compileFragmentShader(fragmentShaderCode);

		String vertexShaderCode = this.loadShaderCode(context.getResources().openRawResource(R.raw.vertex_shader_0));
		int vshd = this.compileVertexShader(vertexShaderCode);
		this.createProgram(0, vshd, fshd, 0);

		textureHandle[0] = GLES20.glGetUniformLocation(glProgram[0],"u_Texture");

		transMatrixHandle[0] = GLES20.glGetUniformLocation(glProgram[0],"u_TransMatrix");
		transVectorHandle[0] = GLES20.glGetUniformLocation(glProgram[0],"u_TransVector");
		projectionMatrixHandle[0] = GLES20.glGetUniformLocation(glProgram[0],"u_ProjeMatrix");
		transYHandle[0] = GLES20.glGetUniformLocation(glProgram[0], "u_TransY");

		vertexShaderCode = this.loadShaderCode(context.getResources().openRawResource(R.raw.vertex_shader_1));
		vshd = this.compileVertexShader(vertexShaderCode);
		this.createProgram(1, vshd, fshd, 0);

		textureHandle[1] = GLES20.glGetUniformLocation(glProgram[1],"u_Texture");

		transMatrixHandle[1] = GLES20.glGetUniformLocation(glProgram[1],"u_TransMatrix");
		projectionMatrixHandle[1] = GLES20.glGetUniformLocation(glProgram[1],"u_ProjeMatrix");
		transYHandle[1] = GLES20.glGetUniformLocation(glProgram[1],"u_TransY");

		vertexShaderCode = this.loadShaderCode(context.getResources().openRawResource(R.raw.vertex_shader_2));
		vshd = this.compileVertexShader(vertexShaderCode);
		this.createProgram(2, vshd, fshd, 0);

		textureHandle[2] = GLES20.glGetUniformLocation(glProgram[2],"u_Texture");
	}

	private void initBuffers()
	{
		//Texture - General
		texVecBuffer.rewind();

		texVecBuffer.put(0.0f).put(1.0f);
		texVecBuffer.put(0.0f).put(0.0f);
		texVecBuffer.put(1.0f).put(1.0f);
		texVecBuffer.put(1.0f).put(0.0f);
		texVecBuffer.rewind();

		//Deck Upper
		deckUVecBuffer.rewind();

		deckUVecBuffer.put(-1.0f).put(1.0f-deckRatio);
		deckUVecBuffer.put(-1.0f).put(1.0f);
		deckUVecBuffer.put(1.0f).put(1.0f-deckRatio);
		deckUVecBuffer.put(1.0f).put(1.0f);
		deckUVecBuffer.rewind();

		//Deck Middle
		deckMVecBuffer.rewind();

		deckMVecBuffer.put(-1.0f).put(-1.0f+deckRatio);
		deckMVecBuffer.put(-1.0f).put(1.0f-deckRatio);
		deckMVecBuffer.put(1.0f).put(-1.0f+deckRatio);
		deckMVecBuffer.put(1.0f).put(1.0f-deckRatio);
		deckMVecBuffer.rewind();

		//Deck Lower
		deckUVecBuffer.rewind();

		deckDVecBuffer.put(-1.0f).put(-1.0f);
		deckDVecBuffer.put(-1.0f).put(-1.0f+deckRatio);
		deckDVecBuffer.put(1.0f).put(-1.0f);
		deckDVecBuffer.put(1.0f).put(-1.0f+deckRatio);
		deckDVecBuffer.rewind();

		//Disc
		discVecBuffer.rewind();

		float dist = (float)Math.min(width, height)/2-10.0f;
		discVecBuffer.put(-dist).put(-dist);
		discVecBuffer.put(-dist).put(dist);
		discVecBuffer.put(dist).put(-dist);
		discVecBuffer.put(dist).put(dist);
		discVecBuffer.rewind();

		//Vinyl
		vinylVecBuffer.rewind();

		float distVnl = dist-0.19f*dist;

		vinylVecBuffer.put(-distVnl).put(-distVnl);
		vinylVecBuffer.put(-distVnl).put(distVnl);
		vinylVecBuffer.put(distVnl).put(-distVnl);
		vinylVecBuffer.put(distVnl).put(distVnl);
		vinylVecBuffer.rewind();

		//Arm
		armVecBuffer.rewind();

		dist-=10.0f;
		float distY = (float)(dist*armRatio);
		armVecBuffer.put(-dist).put(-distY);
		armVecBuffer.put(-dist).put(distY);
		armVecBuffer.put(dist).put(-distY);
		armVecBuffer.put(dist).put(distY);
		armVecBuffer.rewind();
	}

	private void initArmImage()
	{
		Bitmap image = BitmapFactory.decodeResource(context.getResources(),R.drawable.arm);

		armRatio = (float)image.getHeight()/image.getWidth();

		GLES20.glUseProgram(glProgram[0]);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
		GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, GLES20.GL_TRUE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

		GLES20.glUseProgram(0);

		image.recycle();
		image = null;
	}

	private void initGearImage()
	{
		Bitmap image = BitmapFactory.decodeResource(context.getResources(),R.drawable.gear_xxx);

		if(image==null)
		{
			image = BitmapFactory.decodeResource(context.getResources(),R.drawable.gear_xx);

			if(image==null)
			{
				image = BitmapFactory.decodeResource(context.getResources(),R.drawable.gear_x);

				if(image==null)
				{
					image = BitmapFactory.decodeResource(context.getResources(),R.drawable.gear);
				}
			}
		}

		if(image!=null)
		{
			GLES20.glUseProgram(glProgram[1]);
			GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[1]);
			GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, GLES20.GL_TRUE);
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

			GLES20.glUseProgram(0);

			image.recycle();
			image = null;
		}
	}

	private void initDeckImage()
	{
		Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.deck_u);

		deckRatio = (float)image.getHeight()/image.getWidth();

		GLES20.glUseProgram(glProgram[2]);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE3);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[3]);
		GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, GLES20.GL_TRUE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

		image.recycle();
		image = null;

		image = BitmapFactory.decodeResource(context.getResources(), R.drawable.deck_m);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE4);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[4]);
		GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, GLES20.GL_TRUE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

		image.recycle();
		image = null;

		image = BitmapFactory.decodeResource(context.getResources(), R.drawable.deck_d);

		GLES20.glActiveTexture(GLES20.GL_TEXTURE5);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[5]);
		GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, GLES20.GL_TRUE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

		GLES20.glUseProgram(0);

		image.recycle();
		image = null;
	}

	public void loadVinyl(int rsId)
	{
		vinylOn = false;

		new VinylLoaderResource(this).execute(rsId);
	}

	public void loadVinyl(String filename)
	{
		vinylOn = false;

		new VinylLoaderString(this).execute(filename);
	}

	public synchronized void loadVinyl(Bitmap image)
	{
		GLES20.glUseProgram(glProgram[1]);
		GLES20.glActiveTexture(GLES20.GL_TEXTURE2);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[2]);
		GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, GLES20.GL_TRUE);
		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, image, 0);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

		GLES20.glUseProgram(0);

		image.recycle();
		image = null;

		vinylOn = true;
	}

	public void unloadVinyl()
	{
		vinylOn = false;
		GLES20.glClear(GLES20.GL_TEXTURE2);
	}

	private String loadShaderCode(InputStream is)
	{
		StringBuilder vertexCode = new StringBuilder();
		String line;
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			while( (line = reader.readLine()) !=null )
			{
				vertexCode.append(line);
				vertexCode.append('\n');
			}
		}
		catch(Exception e)
		{
			throw new IllegalArgumentException("Unable to load shader from file : ", e);
		}

		return vertexCode.toString();
	}

	private int compileVertexShader(String shaderCode)
	{
		int shd = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);

		GLES20.glShaderSource(shd, shaderCode);
		GLES20.glCompileShader(shd);

		this.checkGlError("Vertex Shader");
		return shd;
	}

	private int compileFragmentShader(String shaderCode)
	{
		int shd = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);

		GLES20.glShaderSource(shd, shaderCode);
		GLES20.glCompileShader(shd);

		return shd;
	}

	private void createProgram(int prg, int verShd, int frgShd, int gmtShd)
	{
		glProgram[prg] = GLES20.glCreateProgram();

		if(verShd!=0)
		{
			GLES20.glAttachShader(glProgram[prg], verShd);
		}
		if(frgShd!=0)
		{
			GLES20.glAttachShader(glProgram[prg], frgShd);
		}
		if(gmtShd!=0)
		{
			GLES20.glAttachShader(glProgram[prg], gmtShd);
		}

		GLES20.glBindAttribLocation(glProgram[prg], 0, "a_VerVector");
		GLES20.glBindAttribLocation(glProgram[prg], 1, "a_TexVector");
		GLES20.glLinkProgram(glProgram[prg]);
		GLES20.glValidateProgram(glProgram[prg]);
	}

	private void draw()
	{
		if(turntable!=null)
		{
			//Transformations//
			turntable.rotateArm();
			turntable.rotateDisc();

			//<Initialize>//
			GLES20.glEnableVertexAttribArray(0);
			GLES20.glEnableVertexAttribArray(1);
			GLES20.glVertexAttribPointer(1, 2, GLES20.GL_FLOAT, false, 8, texVecBuffer);
			//</Initialize>//

			/*---- <Decks> ----*/
			GLES20.glUseProgram(glProgram[2]);

			//<Upper>//
			GLES20.glUniform1i(textureHandle[2], 3);
			GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 8, deckUVecBuffer);
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
			//</Upper>//

			//<Middle>//
			GLES20.glUniform1i(textureHandle[2], 4);
			GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 8, deckMVecBuffer);
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
			//</Middle>//

			//<Lower>//
			GLES20.glUniform1i(textureHandle[2], 5);
			GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 8, deckDVecBuffer);
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
			//</Lower>//

			/*---- </Decks> ----*/

			/*---- <Discs> ----*/
			GLES20.glUseProgram(glProgram[1]);

			//<Gear>//
			GLES20.glUniform1i(textureHandle[1], 1);
			GLES20.glUniformMatrix2fv(transMatrixHandle[1], 1, false, turntable.discMatrix, 0);
			GLES20.glUniform1f(transYHandle[1], turntable.discTransY);
			GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 8, discVecBuffer);
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
			//</Gear>//

			//<Vinyl>//
			if(vinylOn)
			{
				GLES20.glUniform1i(textureHandle[1], 2);
				GLES20.glUniformMatrix2fv(transMatrixHandle[1], 1, false, turntable.discMatrix, 0);
				GLES20.glUniform1f(transYHandle[1], turntable.discTransY);

				GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 8, vinylVecBuffer);

				GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
			}
			//</Vinyl>//
			/*---- </Discs> ----*/

			/*---- <Arm> ----*/
			GLES20.glUseProgram(glProgram[0]);

			GLES20.glUniform1i(textureHandle[0], 0);
			GLES20.glUniformMatrix2fv(transMatrixHandle[0], 1, false, turntable.armMatrix, 0);
			GLES20.glUniform2fv(transVectorHandle[0], 1, turntable.armVector, 0);
			GLES20.glUniform1f(transYHandle[0], turntable.armTransY);
			GLES20.glVertexAttribPointer(0, 2, GLES20.GL_FLOAT, false, 8, armVecBuffer);
			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
			/*---- </Arm> ----*/

			//<Finalize>//
			GLES20.glDisableVertexAttribArray(0);
			GLES20.glDisableVertexAttribArray(1);

			GLES20.glUseProgram(0);
			//</Finalize>//

			if(firstDraw)
			{
				firstDraw = false;
				Intent intent = new Intent(MainActivityReceiver.INIT_TABLE);
				LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
			}
		}
	}

	private void checkGlError(String glOperation)
	{
		int error;
		while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR)
		{
			PostEngin.logDebug(glOperation+": glError "+error);
			throw new RuntimeException(glOperation + ": glError " + error);
		}
	}

	//Variables
	private int width;
	private int height;

	private float armRatio;
	private float deckRatio;

	protected Turntable turntable;
	protected Context context;

	private float[] projectionMatrix;

	private int[] glProgram;
	private int[] textures;

	private FloatBuffer discVecBuffer;
	private FloatBuffer armVecBuffer;
	private FloatBuffer texVecBuffer;
	private FloatBuffer deckUVecBuffer;
	private FloatBuffer deckMVecBuffer;
	private FloatBuffer deckDVecBuffer;
	private FloatBuffer vinylVecBuffer;

	private int[] projectionMatrixHandle;
	private int[] textureHandle;
	private int[] transMatrixHandle;
	private int[] transVectorHandle;
	private int[] transYHandle;

	private volatile boolean firstDraw;
	private volatile boolean vinylOn;
}
