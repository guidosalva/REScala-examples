package examples.bouncing

import rescala.events._
import rescala._
import makro.SignalMacro.{SignalM => Signal}
import swing.{Panel, MainFrame, SimpleSwingApplication}
import java.awt.{Color, Graphics2D, Dimension}
import java.awt.Point
import scala.swing.Swing

object SignalVersion extends SimpleSwingApplication {
  lazy val application = new SignalVersion
  def top = application.frame
  
  override def main(args: Array[String]) {
    super.main(args)
    while (true) {
	  Swing onEDTWait { application.tick() += 1 }
      Thread sleep 20
    }
  }
}

class SignalVersion {
  val Size = 50
  val Max_X = 600
  val Max_Y = 600
  val initPosition = new Point(20, 10)
  val speed = new Point(10,8)
  
  val tick = Var(0)
  
  // Signals for x and y position
  // entirely functionally dependent on time (ticks)
  val x = Signal {
	val width = Max_X - Size
	val d = speed.x * tick() + initPosition.x
	if ((d / width) % 2 == 0) d % width else width - d % width
  }
  val y = Signal {
	val width = Max_Y - Size
	val d = speed.y * tick() + initPosition.y
	if ((d / width) % 2 == 0) d % width else width - d % width
  }
  
  tick.changed += ((_ : Int) => frame.repaint)
  
  // drawing code 
  val frame = new MainFrame {
    contents = new Panel() {
      preferredSize = new Dimension(600, 600)
      override def paintComponent(g: Graphics2D) {
	    g.fillOval(x.get, y.get, Size, Size)
      }
    }
  }
}
