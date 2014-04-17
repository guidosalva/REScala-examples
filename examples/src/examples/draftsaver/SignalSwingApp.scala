package examples.draftsaver

import java.awt.Color
import java.util.Calendar
import scala.swing.BoxPanel
import scala.swing.Button
import scala.swing.Label
import scala.swing.MainFrame
import scala.swing.Orientation
import scala.swing.ScrollPane
import scala.swing.SimpleSwingApplication
import scala.swing.Swing
import scala.swing.TextArea
import scala.swing.event.ButtonClicked
import scala.swing.event.Key
import scala.swing.event.KeyReleased
import javax.swing.BorderFactory
import react.Signal
import macro.SignalMacro.{ SignalM => Signal }
import react.events.ImperativeEvent

// Wrap a bit of Swing, as if it were an FRP library
trait ReactiveText {
  def text_=(s: String)
  def text_=(value: Signal[String]) {
    this.text_=(value.getValue)
    value.changed += { (t: String) => this.text_=(t) }
  }
}
class ReactiveLabel extends Label with ReactiveText
class ReactiveTextArea extends TextArea with ReactiveText
class ReactiveButton extends Button with ReactiveText {
  val clicked = new ImperativeEvent[ButtonClicked] // wrap the event to escala
  reactions += { case c @ ButtonClicked(_) => clicked(c) }
}

object SignalSwingApp extends SimpleSwingApplication {
  def top = new MainFrame {

    /* Create the graphics */
    title = "Draftsaver Example: Reactive Swing App"
    val textArea = new TextArea {
      rows = 5
    }
    val saveButton = new ReactiveButton {
      text = "Save Now"
    }
    val lastSavedLabel = new ReactiveLabel
    val savedDraftTextArea = new ReactiveTextArea {
      rows = 5
      border = BorderFactory.createLineBorder(Color.BLACK)
      editable = false
      background = new Color(240, 240, 240)
    }

    contents = new BoxPanel(Orientation.Vertical) {
      contents += new Label {
        text = "Click Save Now or STRG+S to save the draft. The draft is discarded as soon as you close the application."
      }
      contents += Swing.VStrut(10)
      contents += new ScrollPane(textArea)
      contents += Swing.VStrut(5)
      contents += saveButton
      contents += Swing.VStrut(10)
      contents += lastSavedLabel
      contents += Swing.VStrut(10)
      contents += new Label {
        text = "The currently saved draft is:"
      }
      contents += Swing.VStrut(5)
      contents += new ScrollPane(savedDraftTextArea)
      border = Swing.EmptyBorder(10, 30, 10, 30)
    }

    /* The logic */
    val ctrlSPressed = new ImperativeEvent[Unit]()
    val saveEvent = saveButton.clicked.dropParam || ctrlSPressed

    // TODO why doesn't this version work!?
//    val savedTime = saveEvent.map((_: Unit) => Calendar.getInstance().getTime().toString())
//    lastSavedLabel.text = Signal { "Last Saved: " + savedTime.latest("")() }
    // this version works
    val savedTime = saveEvent.map((_: Unit) => Calendar.getInstance().getTime().toString()).latest("")
    lastSavedLabel.text = Signal { "Last Saved: " + savedTime() }

    val savedText = saveEvent.map((_: Unit) => textArea.text)

    savedDraftTextArea.text = savedText.latest("")

    // this Event/Observer-style code is still needed to detect the CTRL+S shortcut
    listenTo(textArea.keys)
    reactions += {
      case c @ KeyReleased(_, Key.S, _, _) if c.peer.isControlDown() => ctrlSPressed()
    }
  }
}
