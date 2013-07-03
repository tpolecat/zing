package zing

import javax.swing.JComponent
import zwt.Container

// For JComponents that are legitimate containers
trait ZContainer extends Container {
	type Peer <: JComponent
}