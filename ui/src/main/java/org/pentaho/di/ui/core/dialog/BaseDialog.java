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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A base dialog class containing a body and a button panel.
 */
public abstract class BaseDialog extends Dialog {
  private static Class<?> PKG = BaseDialog.class;

  protected Map<String, Listener> buttons = new HashMap();

  private Shell shell;

  private PropsUI props;

  public BaseDialog( final Shell shell ) {
    super( shell, SWT.NONE );
    this.props = PropsUI.getInstance();
  }

  public BaseDialog( final Shell shell, final Map<String, Listener> buttons ) {
    this( shell );
    this.buttons = buttons;
  }

  protected abstract String getDialogTitle();

  /**
   * Returns a {@link SelectionAdapter} that is used to "submit" the dialog.
   */
  protected abstract SelectionAdapter getFieldSelectionAdapter();

  /**
   * Returns the last element in the body - the one to which the buttons should be attached.
   * @return
   */
  protected abstract Control buildBody();

  public int open() {
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.SHEET | SWT.RESIZE );
    props.setLook( shell );
    shell.setImage( GUIResource.getInstance().getImageSpoon() );

    FormLayout formLayout = new FormLayout();
    formLayout.marginWidth = Const.FORM_MARGIN;
    formLayout.marginHeight = Const.FORM_MARGIN;

    shell.setLayout( formLayout );
    shell.setText( getDialogTitle() );

    buildBody();

    final List<Button> buttonList = new ArrayList();
    if ( buttons != null ) {
      for ( final String buttonName : buttons.keySet() ) {
        final Button button = new Button( shell, SWT.PUSH );
        button.setText( BaseMessages.getString( PKG, buttonName ) );
        final Listener listener = buttons.get( buttonName );
        if ( listener != null ) {
          button.addListener( SWT.Selection, listener );
        } else {
          // fall back on simply closing the dialog
          button.addListener( SWT.Selection, new Listener() {
            public void handleEvent( Event e ) {
              dispose();
            }
          } );
        }
        buttonList.add( button );
      }

      BaseStepDialog.positionBottomButtons( shell, (Button[]) buttonList.toArray(), Const.FORM_MARGIN, buildBody() );
    }

    // Detect [X] or ALT-F4 or something that kills this window...
    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        dispose();
      }
    } );

    shell.pack();
    BaseStepDialog.setSize( shell );
    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return 1;
  }

  protected void dispose() {
    props.setScreen( new WindowProperty( shell ) );
    shell.dispose();
  }
}
