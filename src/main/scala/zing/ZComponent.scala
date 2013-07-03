package zing

import javax.swing.JComponent
import zwt.Component

// For JComponents that are not meaningful containers
trait ZComponent extends Component {
	type Peer <: JComponent
}