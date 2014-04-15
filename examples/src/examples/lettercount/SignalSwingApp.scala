package examples.lettercount

import react.events.ImperativeEvent
import react.SignalSynt
import react.Var
import react.Signal
import macro.SignalMacro.{ SignalM => Signal }
import scala.swing._
import scala.swing.event._

// Wrap a bit of Swing, as if it were an FRP library
trait ReactiveText {
  def text_=(s: String)
  def text_=(value: Signal[String]) {
    this.text_=(value.getValue)
    value.changed += { (t: String) => this.text_=(t) }
  }
}
class ReactiveLabel extends Label with ReactiveText
class ReactiveTextField extends TextField {
  val keyReleased = new ImperativeEvent[KeyReleased] // wrap the event to escala
  // we have to listen to the keys, otherwise there is no event on each key hit
  listenTo(this.keys)
  reactions += { case c @ KeyReleased(_, _, _, _) => keyReleased(c) }
}

// The application
object SignalSwingApp extends SimpleSwingApplication {

  def top = new MainFrame {
    title = "Letter Count Example: Reactive Swing App"

    val textLabel = new Label {
      text = "Text buffer: "
    }
    val label = new ReactiveLabel
    val textField = new ReactiveTextField {
      columns = 20
    }

    val length = textField.keyReleased.iterate(0) { (_) => textField.text.length() }

    // Signal to set label text
    label.text = Signal { "Length: " + length() }

    contents = new BoxPanel(Orientation.Vertical) {
      contents += new BoxPanel(Orientation.Horizontal) {
        contents += textLabel
        contents += textField
      }
      contents += label
      border = Swing.EmptyBorder(30, 30, 10, 30)
    }
  }
}
