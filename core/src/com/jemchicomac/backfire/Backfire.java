package com.jemchicomac.backfire;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.viewport.FitViewport;

/*public class Backfire extends ApplicationAdapter {
	final float VIRTUAL_WIDTH = 16f;
	final float VIRTUAL_HEIGHT = 9f; // +++ The virtual height is 4 meters

    OrthographicCamera cam;
    SpriteBatch batch;
    Texture texture;
    FitViewport viewport;
    
    float y;
    float gravity = -9.81f; // earth gravity is +/- 9.81 m/s^2 downwards
    float velocity;
    float jumpHeight = 1f; // jump 1 meter every time

    public void create () {
    
    	batch = new SpriteBatch();
        texture = new Texture("badlogic.jpg");
        cam = new OrthographicCamera();
        
        viewport = new FitViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, cam);
    }

    public void resize (int width, int height) {
    	
    	viewport.update(width, height);
        //cam.setToOrtho(false, VIRTUAL_HEIGHT * width / (float)height, VIRTUAL_HEIGHT);
        //batch.setProjectionMatrix(cam.combined);
    }

    public void render () {
        if (Gdx.input.justTouched()) y += jumpHeight;

        float delta = Math.min(1 / 10f, Gdx.graphics.getDeltaTime());
        velocity += gravity * delta;
        y += velocity * delta;
        if (y <= 0) y = velocity = 0;

        
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(texture, 0, y, 140f, 140f); // +++
        batch.end();
    }

    public void dispose () {
        texture.dispose();
        batch.dispose();
    }
}*/

/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** Cycles viewports while rendering a stage with a root Table for the layout. */
public class Backfire extends ApplicationAdapter {
	
	public static final float RUNNING_FRAME_DURATION = 0.09f;
	
	static final int VIRTUAL_WIDTH 	= 	480; 	// x4 in 1080p
    static final int VIRTUAL_HEIGHT = 	270;	// x4 in 1080p
    static final int DEFAULT_POLICY = 	2;	// Fitview
    
    private OrthographicCamera cam;
	
	Array<Viewport> viewports;
	Array<String> names;
	Stage stage;
	Label label;
	
	BitmapFont retroFont;
	private SpriteBatch spriteBatch;
	
	/* Textures for Player */
	private TextureRegion playerIdleLeft;
	private TextureRegion playerIdleRight;
	private TextureRegion playerJumpLeft;
	private TextureRegion playerJumpRight;
	
	/* Animations for Player */
	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;	
	
	float stateTime;
	TextureRegion currentFrame;
	
	public void create () {
		stage = new Stage();
		Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"));

		label = new Label("", skin);

		Table root = new Table(skin);
		root.setFillParent(true);
		root.setBackground(skin.getDrawable("default-pane"));
		root.debug().defaults().space(6);
		root.add(new TextButton("Button 1", skin));
		root.add(new TextButton("Button 2", skin)).row();
		root.add("Press spacebar to change the viewport:").colspan(2).row();
		root.add("Press 'F' for fullscreen and 'ESC' for exit").colspan(2).row();
		
		root.add(label).colspan(2);
		stage.addActor(root);

		// Setting up camera
		
		cam = new OrthographicCamera(VIRTUAL_WIDTH,VIRTUAL_HEIGHT);
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
		//cam.position.set(cam.viewportWidth, cam.viewportHeight, 0);
        //cam.update();
		
		//viewports = getViewports(stage.getCamera());
        
        viewports = getViewports(cam);
        
		names = getViewportNames();

		stage.setViewport(viewports.get(DEFAULT_POLICY));
		label.setText(names.get(DEFAULT_POLICY));
		
		spriteBatch = new SpriteBatch();
		
		cam.update();
		
		// Player
		
		loadPlayerTextures();
		
		// Input processor

		Gdx.input.setInputProcessor(new InputMultiplexer(new InputAdapter() {
			public boolean keyDown (int keycode) {
				if (keycode == Input.Keys.SPACE) {
					int index = (viewports.indexOf(stage.getViewport(), true) + 1) % viewports.size;
					label.setText(names.get(index));
					Viewport viewport = viewports.get(index);
					stage.setViewport(viewport);
					resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				}
				
				if (keycode == Input.Keys.F) {
					Gdx.graphics.setDisplayMode(Gdx.graphics.getDesktopDisplayMode().width, Gdx.graphics.getDesktopDisplayMode().height, true);
				}
				
				if (keycode == Input.Keys.A) {
		            cam.zoom += 0.02;
		        }
		        if (keycode == Input.Keys.Q) {
		            cam.zoom -= 0.02;    
		        }
				
				if (keycode == Keys.ESCAPE) Gdx.app.exit();
				
				return false;
			}
		}, stage));
	}

	public void render () {
		
		stage.act();

		cam.update();
		//spriteBatch.setProjectionMatrix(stage.getCamera().combined);
		
		Gdx.gl.glClearColor(0, 1, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.draw();
		spriteBatch.setProjectionMatrix(cam.combined);
		
		// Run the animation
		
		stateTime += Gdx.graphics.getDeltaTime();           
        currentFrame = walkRightAnimation.getKeyFrame(stateTime, true);
        
        float xDelta = stateTime*10;
        
        spriteBatch.begin();
        spriteBatch.draw(currentFrame, 100 + xDelta , 100);
        spriteBatch.end();
		
	}

	public void resize (int width, int height) {
		stage.getViewport().update(width, height, false);
	}

	public void dispose () {
		stage.dispose();
	}

	static public Array<String> getViewportNames () {
		Array<String> names = new Array();
		names.add("StretchViewport");
		names.add("FillViewport");
		names.add("FitViewport");
		names.add("ExtendViewport: no max");
		names.add("ExtendViewport: max");
		names.add("ScreenViewport: 1:1");
		names.add("ScreenViewport: 0.75:1");
		names.add("ScalingViewport: none");
		return names;
	}

	static public Array<Viewport> getViewports (Camera camera) {
		//int minWorldWidth = 640;
		//int minWorldHeight = 480;
		//int maxWorldWidth = 800;
		//int maxWorldHeight = 480;

		int minWorldWidth  = VIRTUAL_WIDTH;
		int minWorldHeight = VIRTUAL_HEIGHT;
		int maxWorldWidth  = VIRTUAL_WIDTH;
		int maxWorldHeight = VIRTUAL_HEIGHT;

		
		Array<Viewport> viewports = new Array();
		viewports.add(new StretchViewport(minWorldWidth, minWorldHeight, camera));
		viewports.add(new FillViewport(minWorldWidth, minWorldHeight, camera));
		viewports.add(new FitViewport(minWorldWidth, minWorldHeight, camera));
		viewports.add(new ExtendViewport(minWorldWidth, minWorldHeight, camera));
		viewports.add(new ExtendViewport(minWorldWidth, minWorldHeight, maxWorldWidth, maxWorldHeight, camera));
		viewports.add(new ScreenViewport(camera));

		ScreenViewport screenViewport = new ScreenViewport(camera);
		screenViewport.setUnitsPerPixel(0.75f);
		viewports.add(screenViewport);

		viewports.add(new ScalingViewport(Scaling.none, minWorldWidth, minWorldHeight, camera));
		return viewports;
	}
	
	public void loadPlayerTextures(){
		 
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("data/solbrain.pack"));
 
		/* Standing */
		playerIdleLeft = atlas.findRegion("1");
 
		playerIdleRight = new TextureRegion(playerIdleLeft);
		playerIdleRight.flip(true, false);
 
		TextureRegion[] walkLeftFrames = new TextureRegion[6];
		
		for (int i = 0; i < 6; i++) {
			walkLeftFrames[i] =  atlas.findRegion(((i+6)+""));
		}
 
		walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION, walkLeftFrames);
 
		TextureRegion[] walkRightFrames = new TextureRegion[6];
		for (int i = 0; i < 6; i++) {
			walkRightFrames[i] = new TextureRegion(walkLeftFrames[i]);
			walkRightFrames[i].flip(true, false);
		}
 
		walkRightAnimation = new Animation(RUNNING_FRAME_DURATION, walkRightFrames);
 
		playerJumpLeft = atlas.findRegion("3");
		playerJumpRight = new TextureRegion(playerJumpLeft);
		playerJumpRight.flip(true, false);
 
	}
	
	
}
