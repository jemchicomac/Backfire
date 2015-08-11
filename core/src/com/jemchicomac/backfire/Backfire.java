package com.jemchicomac.backfire;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	Array<Viewport> viewports;
	Array<String> names;
	Stage stage;
	Label label;
	
	BitmapFont retroFont;
	
	
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

		viewports = getViewports(stage.getCamera());
		names = getViewportNames();

		stage.setViewport(viewports.first());
		label.setText(names.first());

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
				
				if (keycode == Keys.ESCAPE) Gdx.app.exit();
				
				return false;
			}
		}, stage));
	}

	public void render () {
		stage.act();

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.draw();
	}

	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
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
		int minWorldWidth = 640;
		int minWorldHeight = 480;
		int maxWorldWidth = 800;
		int maxWorldHeight = 480;

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
}
