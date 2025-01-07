package com.anax.graphtool
package window

import java.awt.AWTEvent
import java.awt.event.{KeyAdapter, KeyEvent, MouseAdapter, MouseEvent, MouseWheelEvent}
import java.awt.image.BufferedImage
import java.util.concurrent.ConcurrentHashMap
import javax.swing.*

class Window(val width: Int,val height: Int) {
	
	val keysDown: ConcurrentHashMap[Int, Boolean] = new ConcurrentHashMap[Int, Boolean]()
	val unprocessedKeyPresses: ConcurrentHashMap[Int, Boolean] = new ConcurrentHashMap[Int, Boolean]()
	
	@volatile var isMouseRightDown = false
	@volatile var isMouseLeftDown = false
	@volatile var isUnprocessedMouseRightPress = false
	@volatile var isUnprocessedMouseLeftPress = false
	@volatile var unprocessedDragDX = 0
	@volatile var unprocessedDragDY = 0
	@volatile var unprocessedWheelScroll = 0
	
	var mouseDraggedListener: () => Unit = ()=>()
	var mouseScrolledListener: () => Unit = ()=>()
	var keyPressedListener: () => Unit = ()=>()
	var mousePressedListener: () => Unit = ()=>()
	var mouseReleasedListener: () => Unit = ()=>()
	
	private var lastMouseX: Int = 0
	private var lastMouseY: Int = 0
	
	val label: JLabel = new JLabel()
	val panel: JPanel = new JPanel()
	val frame: JFrame = new JFrame()
	panel.add(label)
	frame.add(panel)
	
	frame.addKeyListener(new KeyAdapter {
		override def keyPressed(e: KeyEvent): Unit = {
			keysDown.put(e.getKeyCode, true)
			unprocessedKeyPresses.put(e.getKeyCode, true)
			keyPressedListener()
			super.keyPressed(e);
		}
	 
		override def keyReleased(e: KeyEvent): Unit = {
			keysDown.put(e.getKeyCode, false)
			super.keyReleased(e)
		}
	})
	
	label.addMouseListener(new MouseAdapter {
		override def mousePressed(e: MouseEvent): Unit = {
			if(e.getButton == MouseEvent.BUTTON1){
				isMouseLeftDown = true
				isUnprocessedMouseLeftPress = true
			}
			if (e.getButton == MouseEvent.BUTTON3){
				isMouseRightDown = true
				isUnprocessedMouseRightPress = true
			}
			mousePressedListener()
			super.mousePressed(e)
		}
	 
		override def mouseReleased(e: MouseEvent): Unit = {
			if (e.getButton == MouseEvent.BUTTON1){
				isMouseLeftDown = false
			}
			if (e.getButton == MouseEvent.BUTTON2){
				isMouseRightDown = false
			}
			mouseReleasedListener()
			super.mouseReleased(e)
		}
	})
	
	label.addMouseWheelListener(new MouseAdapter {
		override def mouseWheelMoved(e: MouseWheelEvent): Unit = {
			unprocessedWheelScroll += e.getUnitsToScroll
			mouseScrolledListener()
		}
	})
	
	label.addMouseMotionListener(new MouseAdapter {
		override def mouseMoved(e: MouseEvent): Unit = {
			lastMouseX = e.getX
			lastMouseY = e.getY
		}
		
		override def mouseDragged(e: MouseEvent): Unit = {
			if(!keysDown.getOrDefault(KeyEvent.VK_SHIFT, false)) {
				val dx = e.getX - lastMouseX
				val dy = e.getY - lastMouseY
				
				unprocessedDragDX += dx
				unprocessedDragDY += dy
				
				mouseDraggedListener()
			}
			lastMouseX = e.getX
			lastMouseY = e.getY
		}
	})

	frame.setSize(width, height)
	frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
	frame.setResizable(true)
	frame.setVisible(true)

	def setImage(image: BufferedImage): Unit = {
		label.setIcon(new ImageIcon(image))
		SwingUtilities.updateComponentTreeUI(frame)
	}
}