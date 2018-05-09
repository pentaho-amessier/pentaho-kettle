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

package org.pentaho.di.ui.trans.step.common;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.ui.core.dialog.SimpleOkCancelDialog;

import java.util.List;

public class FieldSelectionDialog extends SimpleOkCancelDialog {
  private static Class<?> PKG = FieldSelectionDialog.class;

  private String[] fieldNames;

  private CommonStepDialog parentDialog;
  private Button newFieldsOnly;
  private Button clearAndAddAll;

  private Group fieldSelectionGrp;

  public FieldSelectionDialog( final CommonStepDialog parentDialog, final String[] fieldNames ) {
    super( parentDialog.getParent() );
    this.parentDialog = parentDialog;
    this.fieldNames = fieldNames;
  }

  protected SelectionAdapter getFieldSelectionAdapter() {
    return new SelectionAdapter() {
      @Override
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };
  }

  /*
  System.FileType.Job=Job
System.FileType.Transformation=Transformation
System.FileType.File=File

System.GetFields.SampleSize.Dialog.Title=Sample size
System.GetFields.SampleSize.Dialog.Message=Number of lines to sample\:
System.GetFields.SampleSize.Dialog.ShowSample.Message=Show sample summary

System.GetFields.NewFieldsFound.Title=New fields were found
System.GetFields.NewFieldsFound.Message=We have found {0} new fields. What would you like to do with the new fields?
System.GetFields.AddNewOnly.Label=Add new fields only
System.GetFields.ClearAndAddAll.Label=Clear and add all fields

System.GetFields.NoNewFields.Title=No new fields were found
System.GetFields.NoNewFields.Message=We were unable to find any new incoming fields.
   */

  @Override
  protected Control buildBody() {
    fieldSelectionGrp = new Group(parentDialog.getParent(), SWT.NONE);
    fieldSelectionGrp.setLayout(new RowLayout(SWT.VERTICAL));

    newFieldsOnly = new Button( fieldSelectionGrp, SWT.RADIO );
    newFieldsOnly.setText( BaseMessages.getString( PKG, "System.GetFields.AddNewOnly.Label" ) );

    clearAndAddAll = new Button( fieldSelectionGrp, SWT.RADIO );
    clearAndAddAll.setText( BaseMessages.getString( PKG, "System.GetFields.ClearAndAddAll.Label" ) );

    // if fields exits
    return null;
  }

  @Override
  protected String getDialogTitle() {
    return BaseMessages.getString( PKG, "System.GetFields.SampleSize.Dialog.Title" );
  }

  @Override
  protected void ok() {
    try {
      if ( newFieldsOnly.getSelection() ) {
        parentDialog.loadFields( fieldNames, false );
      } else if ( clearAndAddAll.getSelection() ) {
        parentDialog.loadFields( fieldNames, true );
      }
    } catch ( final KettleException e ) {
      // TODO
    }
  }
}
