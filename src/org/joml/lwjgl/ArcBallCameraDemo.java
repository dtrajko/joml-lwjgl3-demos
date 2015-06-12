package org.joml.lwjgl;

import static org.lwjgl.glfw.Callbacks.errorCallbackPrint;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.camera.ArcBallCamera;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class ArcBallCameraDemo {
	GLFWErrorCallback errorCallback;
	GLFWKeyCallback keyCallback;
	GLFWFramebufferSizeCallback fbCallback;
	GLFWCursorPosCallback cpCallback;
	GLFWScrollCallback sCallback;
	GLFWMouseButtonCallback mbCallback;

	long window;
	int width;
	int height;
	int x, y;
	float zoom = 20;
	int mouseX, mouseY;
	boolean down;

	void run() {
		try {
			init();
			loop();

			glfwDestroyWindow(window);
			keyCallback.release();
			fbCallback.release();
			cpCallback.release();
			sCallback.release();
			mbCallback.release();
		} finally {
			glfwTerminate();
			errorCallback.release();
		}
	}

	ArcBallCamera cam = new ArcBallCamera();

	void init() {
		glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
		if (glfwInit() != GL11.GL_TRUE)
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure our window
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

		int WIDTH = 800;
		int HEIGHT = 600;

		window = glfwCreateWindow(WIDTH, HEIGHT, "Hello ArcBall Camera!", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
				if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
					glfwSetWindowShouldClose(window, GL_TRUE);

				if (key == GLFW_KEY_ENTER && action == GLFW_PRESS) {
					cam.center((float) Math.random() * 20.0f - 10.0f, 0.0f, (float) Math.random() * 20.0f - 10.0f);
				}
			}
		});
		glfwSetFramebufferSizeCallback(window, fbCallback = new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long window, int w, int h) {
				if (w > 0 && h > 0) {
					width = w;
					height = h;
				}
			}
		});
		glfwSetCursorPosCallback(window, cpCallback = new GLFWCursorPosCallback() {
			@Override
			public void invoke(long window, double xpos, double ypos) {
				x = (int) xpos - width / 2;
				y = height / 2 - (int) ypos;
			}
		});
		glfwSetMouseButtonCallback(window, mbCallback = new GLFWMouseButtonCallback() {
			@Override
			public void invoke(long window, int button, int action, int mods) {
				if (action == GLFW_PRESS) {
					down = true;
					mouseX = x;
					mouseY = y;
				} else if (action == GLFW_RELEASE) {
					down = false;
				}
			}
		});
		glfwSetScrollCallback(window, sCallback = new GLFWScrollCallback() {
			@Override
			public void invoke(long window, double xoffset, double yoffset) {
				if (yoffset > 0) {
					zoom /= 1.1f;
				} else {
					zoom *= 1.1f;
				}
			}
		});

		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(window, (GLFWvidmode.width(vidmode) - WIDTH) / 2, (GLFWvidmode.height(vidmode) - HEIGHT) / 2);

		glfwMakeContextCurrent(window);
		glfwSwapInterval(0);
		glfwShowWindow(window);
	}

	void renderCube() {
		glBegin(GL_QUADS);
		glColor3f(1.0f, 1.0f, 0.0f);
		glVertex3f(0.5f, -0.5f, -0.5f);
		glVertex3f(0.5f, 0.5f, -0.5f);
		glVertex3f(-0.5f, 0.5f, -0.5f);
		glVertex3f(-0.5f, -0.5f, -0.5f);
		glColor3f(0.0f, 1.0f, 1.0f);
		glVertex3f(0.5f, -0.5f, 0.5f);
		glVertex3f(0.5f, 0.5f, 0.5f);
		glVertex3f(-0.5f, 0.5f, 0.5f);
		glVertex3f(-0.5f, -0.5f, 0.5f);
		glColor3f(1.0f, 0.0f, 1.0f);
		glVertex3f(0.5f, -0.5f, -0.5f);
		glVertex3f(0.5f, 0.5f, -0.5f);
		glVertex3f(0.5f, 0.5f, 0.5f);
		glVertex3f(0.5f, -0.5f, 0.5f);
		glColor3f(0.0f, 1.0f, 0.0f);
		glVertex3f(-0.5f, -0.5f, 0.5f);
		glVertex3f(-0.5f, 0.5f, 0.5f);
		glVertex3f(-0.5f, 0.5f, -0.5f);
		glVertex3f(-0.5f, -0.5f, -0.5f);
		glColor3f(0.0f, 0.0f, 1.0f);
		glVertex3f(0.5f, 0.5f, 0.5f);
		glVertex3f(0.5f, 0.5f, -0.5f);
		glVertex3f(-0.5f, 0.5f, -0.5f);
		glVertex3f(-0.5f, 0.5f, 0.5f);
		glColor3f(1.0f, 0.0f, 0.0f);
		glVertex3f(0.5f, -0.5f, -0.5f);
		glVertex3f(0.5f, -0.5f, 0.5f);
		glVertex3f(-0.5f, -0.5f, 0.5f);
		glVertex3f(-0.5f, -0.5f, -0.5f);
		glEnd();
	}

	void renderGrid() {
		glBegin(GL_LINES);
		glColor3f(0.2f, 0.2f, 0.2f);
		for (int i = -20; i <= 20; i++) {
			glVertex3f(-20.0f, 0.0f, i);
			glVertex3f(20.0f, 0.0f, i);
			glVertex3f(i, 0.0f, -20.0f);
			glVertex3f(i, 0.0f, 20.0f);
		}
		glEnd();
	}

	void loop() {
		GLContext.createFromCurrent();

		// Set the clear color
		glClearColor(0.9f, 0.9f, 0.9f, 1.0f);
		// Enable depth testing
		glEnable(GL_DEPTH_TEST);

		// Remember the current time.
		long lastTime = System.nanoTime();

		Matrix4f mat = new Matrix4f();
		// FloatBuffer for transferring matrices to OpenGL
		FloatBuffer fb = BufferUtils.createFloatBuffer(16);

		float alpha = -20.0f;
		float beta = 20.0f;

		while (glfwWindowShouldClose(window) == GL_FALSE) {
			/* Set input values for the camera */
			if (down) {
				alpha += (x - mouseX) * 0.1f;
				beta += (mouseY - y) * 0.1f;
				mouseX = x;
				mouseY = y;
			}
			cam.alpha(alpha);
			cam.beta(beta);
			cam.zoom(zoom);

			/* Compute delta time */
			long thisTime = System.nanoTime();
			float diff = (float) ((thisTime - lastTime) / 1E9);
			lastTime = thisTime;
			/* And let the camera make its update */
			cam.update(diff);

			glViewport(0, 0, width, height);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			mat.setPerspective(45.0f, (float) width / height, 0.01f, 100.0f).get(fb);
			glMatrixMode(GL_PROJECTION);
			glLoadMatrixf(fb);

			/*
			 * Obtain the camera's view matrix and render grid.
			 */
			cam.viewMatrix(mat.identity()).get(fb);
			glMatrixMode(GL_MODELVIEW);
			glLoadMatrixf(fb);
			renderGrid();

			/* Translate to cube position and render cube */
			mat.translate(cam.centerMover.target).get(fb);
			glLoadMatrixf(fb);
			renderCube();

			glfwSwapBuffers(window);
			glfwPollEvents();
		}
	}

	public static void main(String[] args) {
		new ArcBallCameraDemo().run();
	}
}