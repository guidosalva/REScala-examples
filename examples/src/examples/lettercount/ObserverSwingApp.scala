package examples.lettercount

import scala.swing._
import scala.swing.event._

object ObserverSwingApp extends SimpleSwingApplication {
  def top = new MainFrame {

    /* Create the graphics */
    title = "Letter Count Example: Observer Swing App"
    val textLabel = new Label {
      text = "Text buffer: "
    }
    val textField = new TextField {
      columns = 20
    }
    val label = new Label {
      text = "Length: 0"
    }
    contents = new BoxPanel(Orientation.Vertical) {
      contents += new BoxPanel(Orientation.Horizontal) {
        contents += textLabel
        contents += textField
      }
      contents += label
      border = Swing.EmptyBorder(30, 30, 10, 30)
    }

    /* The logic */
    listenTo(textField.keys)
    reactions += {
      case KeyReleased(_, _, _, _) =>
        label.text = "Length: " + textField.text.length()
    }
  }
}
