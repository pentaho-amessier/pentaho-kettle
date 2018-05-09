/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2018 by Hitachi Vantara : http://www.pentaho.com
 *
 *******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/

package org.pentaho.di.ui.core.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.pentaho.di.core.Const;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.core.gui.GUIResource;
import org.pentaho.di.ui.core.gui.WindowProperty;
import org.pentaho.di.ui.trans.step.BaseStepDialog;

import java.util.HashMap;
import java.util.Map;

/**
 * A convenience implementation of a dialog with an OK and Cancel buttons. Listeners for the buttons can be provided
 * by the caller.
 */
public abstract class SimpleOkCancelDialog extends BaseDialog {
  private static Class<?> PKG = SimpleOkCancelDialog.class;

  private Button wOK, wCancel;

  public SimpleOkCancelDialog( final Shell shell ) {
    super( shell );
    buttons = new HashMap();
    buttons.put( BaseMessages.getString( PKG, "System.Button.OK" ), new Listener() {
      public void handleEvent( Event e ) {
        ok();
      }
    } );
    buttons.put( BaseMessages.getString( PKG, "System.Button.Cancel" ), new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    } );
  }

  /**
   * Override to provide specific behavior, other than just disposing the dialog.
   */
  protected void cancel() {
    dispose();
  }

  /**
   * Override to provide specific behavior, other than just disposing the dialog.
   */
  protected void ok() {
    dispose();
  }
}
