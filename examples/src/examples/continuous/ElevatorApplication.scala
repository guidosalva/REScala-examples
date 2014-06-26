package examples.continuous
import scala.swing._
import java.awt.Color
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import scala.swing.event.ButtonClicked
import rescala.events.ImperativeEvent
import rescala.SignalSynt
import rescala.Var
import rescala.Signal
import makro.SignalMacro.{SignalM => Signal}
import rescala.commons.time._

object ElevatorApplication extends App {
  val elevator = new Elevator(4)
  val app = new ElevatorFrame(elevator)
  app.main(Array())
  Timer.runAll
}

class ElevatorFrame(val elevator : Elevator) extends SimpleSwingApplication {
  // drawing code
  def top = frame 
  val frame = new MainFrame {
    contents = new GridPanel(0,2) {
      contents += new ElevatorPainter(elevator)
      contents += new GridPanel(elevator.nFloors, 1) {
          for(i <- 0 to elevator.nFloors - 1){
            vGap = 8
            contents += new Button {
              this.bounds
              text = elevator.nameOfFloor(i)
              reactions += {case _ => elevator callToFloor i}              
            }
          }
	   }
    }
  }  
  elevator.position.changed += {_ => frame.repaint()}
}

  
class ElevatorPainter(e : Elevator) extends Panel {
  val FloorHeight = e.FloorHeight
  val FloorWidth = (0.9 * e.FloorHeight).asInstanceOf[Int]
  val sizeX = FloorWidth + 50
  val sizeY = FloorHeight * e.nFloors + 50
  
  preferredSize = new Dimension(sizeX, sizeY)
  
  override def paintComponent(g: Graphics2D) {
    draw(g, new Rectangle(0,0,0,0))
  }
  
  def draw(g: Graphics2D, area : Rectangle) {  
	  val FloorX = area.x
	  val FloorY = area.y + e.FloorStart
	  
	 def drawCart(x : Int, y : Int, w : Int, h : Int ){
	    g.setColor(Color.DARK_GRAY)
	    g.fill3DRect(x,y,w,h, true)
	    
	    g.setColor(Color.GRAY)
	    g.fill3DRect(x + 2, y + 2, w / 2 - 2, h - 4, true)
	    g.fill3DRect(x + w / 2 + 2, y + 2, w / 2 - 4, h - 4, true)
	  }
	  
	def drawDuct {
	  g.setColor(Color.BLACK)
	  g.fillRect(FloorX + 5, FloorY + 5, FloorWidth - 10, FloorY + e.nFloors * FloorHeight + 10)
	}
	
	def drawRoom(y : Int){
	  g.setColor(new Color(30,30,100))
	  g.fillRect(FloorX + 8, FloorY + 8 + y, FloorWidth - 16, FloorHeight - 4)
	}	
	
    drawDuct
	e.FloorPos.foreach(drawRoom(_))	
	val pos = e.position()
    drawCart(FloorX + 8, FloorY + 8 + pos, FloorWidth - 16, FloorHeight - 4)    
  }  
}
