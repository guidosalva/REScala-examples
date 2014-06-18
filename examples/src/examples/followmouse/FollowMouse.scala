package examples.followmouse

import examples.followmouse._
import rescala.events.ImperativeEvent
import rescala.SignalSynt
import rescala.Var
import rescala.Signal
import makro.SignalMacro.{ SignalM => Signal }
import swing.{ Panel, MainFrame, SimpleSwingApplication }
import java.awt.{ Color, Graphics2D, Dimension }
import java.awt.Point
import scala.swing.Swing
import scala.swing.event._
import java.awt.Font


object FollowMouse extends SimpleSwingApplication {
  lazy val application = new FollowMouse
  def top = application.frame
  
  override def main(args: Array[String]) {
    super.main(args)
    while (true) {
	  Swing onEDTWait { application.tick() }
      Thread sleep 10
    }
  }
}

class FollowMouse {
  
  val Max_X = 700
  val Max_Y = 600
  val Size = 20
  val Range = 100
  
  
  // The whole logic
  
  val tick = new ImperativeEvent[Unit]  
  val time = tick.iterate(0.0){ acc: Double => (acc + 0.1) % (math.Pi * 2)}  
  
  val mouse = new Mouse
  val mouseX = Signal { mouse.position().getX.toInt - Size / 2 }
  val mouseY = Signal { mouse.position().getY.toInt - Size / 2 }
  
  val xOffset = Signal { math.sin(time()) * Range }
  val yOffset = Signal { math.cos(time()) * Range }
  
  val x = Signal { mouseX() + xOffset().toInt }
  val y = Signal { mouseY() + yOffset().toInt }
  
  
  
  
  // redraw code
  val stateChanged = mouse.position.changed || tick  
  stateChanged += { _ => frame.repaint() }

  // drawing code
  def top = frame
  val frame: MainFrame = new MainFrame {
    title = "Rotating around the mouse"
    resizable = false
    contents = new Panel() {
      
    	/** forward mouse events to EScala wrapper class. Should be replaced once reactive GUI lib is complete */
      listenTo(mouse.moves, mouse.clicks)
      reactions += FollowMouse.this.mouse.react



      preferredSize = new Dimension(Max_X, Max_Y)
      val scoreFont = new Font("Tahoma", java.awt.Font.PLAIN, 32)
      override def paintComponent(g: Graphics2D) {
        g.setColor(java.awt.Color.DARK_GRAY)
        g.fillOval(x.get, y.get, Size, Size)
      }
    }
  }
}
