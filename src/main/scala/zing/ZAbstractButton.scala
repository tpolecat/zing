package zing

import javax.swing.AbstractButton

trait ZAbstractButton extends ZComponent {
  type Peer <: AbstractButton
}