package examples.tempconverter


// EScala lib + behaviour extensions
import rescala.events.ImperativeEvent
import rescala.SignalSynt
import rescala.Var
import rescala.Signal
import makro.SignalMacro.{SignalM => Signal}

// Scala swing events
import swing._
import event._
import scala.swing._
import scala.swing.event._


object SignalTempConverter extends SimpleSwingApplication {
   
  def top = new MainFrame {
    title = "Celsius/Fahrenheit Converter"
    object celsius extends ReactiveTextfield { columns = 5 }
    object fahrenheit extends ReactiveTextfield { columns = 5 }
  
    // two global variables, holding the state
    val degree_f = Var(0)
    val degree_c = Var(0)
    
    // content of the textfields is well-defined
    fahrenheit.text = Signal {degree_c() * 9 / 5 + 32 + ""}
    celsius.text = Signal {(degree_f() - 32) * 5 / 9 + ""}
    
    // listener only changes model, doesn't care about the 'view' (setting the text of the textfields)
    listenTo(celsius, fahrenheit)
    reactions += {
      case EditDone(`fahrenheit`) => degree_f() = fahrenheit.text.toInt
      case EditDone(`celsius`) => degree_c() = celsius.text.toInt
    }
    
      contents = new FlowPanel {
      contents += celsius
      contents += new Label(" Celsius  =  ")
      contents += fahrenheit
      contents += new Label(" Fahrenheit")
      border = Swing.EmptyBorder(15, 10, 10, 10)
    }
  }
}
