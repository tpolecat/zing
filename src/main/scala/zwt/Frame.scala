package zwt

import java.awt

trait Frame extends Window {
	type Peer <: awt.Frame
}