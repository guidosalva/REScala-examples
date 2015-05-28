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

object ObserverSwingApp extends SimpleSwingApplication {
  def top = new MainFrame {

    /* Create the graphics */
    title = "Draftsaver Example: Observer Swing App"
    val textArea = new TextArea {
      rows = 5
    }
    val saveButton = new Button {
      text = "Save Now"
    }
    val lastSavedLabel = new Label {
      text = "Last Saved: "
    }
    val savedDraftTextArea = new TextArea {
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
    listenTo(saveButton)
    listenTo(textArea.keys)
    reactions += {
      case ButtonClicked(_) => saveDraft
      case c @ KeyReleased(_, Key.S, _, _) if c.peer.isControlDown() => saveDraft
    }

    def saveDraft = {
      lastSavedLabel.text = "Last Saved: " + Calendar.getInstance().getTime()
      savedDraftTextArea.text = textArea.text
    }
  }
}
