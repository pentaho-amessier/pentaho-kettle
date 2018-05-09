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
import org.eclipse.swt.widgets.TableItem;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.common.CommonStepMeta;
import org.pentaho.di.ui.core.dialog.EnterNumberDialog;
import org.pentaho.di.ui.core.dialog.EnterTextDialog;

import java.util.List;

public class GetFieldsSampleSizeDialog extends EnterNumberDialog {

  private static Class<?> PKG = GetFieldsSampleSizeDialog.class;

  private CommonStepDialog parentDialog;
  private boolean reloadAll;
  private String[] fieldNames;

  public GetFieldsSampleSizeDialog( final CommonStepDialog parentDialog, final String[] fieldNames,
                                    final boolean reloadAll ) {
    super( parentDialog.getParent(), 100, BaseMessages.getString( PKG, "System.GetFields.SampleSize.Dialog.Title" ),
      BaseMessages.getString( PKG, "System.GetFields.SampleSize.Dialog.Message" ), true,
      BaseMessages.getString( PKG, "System.GetFields.SampleSize.Dialog.ShowSample.Message" ) );
    this.parentDialog = parentDialog;
    this.fieldNames = fieldNames;
    this.reloadAll = reloadAll;
  }
/*
  public void open( final CommonStepMeta meta, final TransMeta transMeta )  {
    int samples = super.open();
    if ( samples >= 0 ) {

      if ( reloadAll ) {
        parentDialog.m_fieldsView.table.removeAll();
      } else {
        // TODO: remove
        int dummy = 0;
      }
      // Update the GUI
      //
      for ( int i = 0; i < fieldNames.length; i++ ) {
        // if reloadAll is true, and the field already exists, update it, otherwise create a new field
        TableItem item = parentDialog.findTableItem( fieldNames[ i ] );
        if ( item == null ) {
          item = new TableItem( parentDialog.m_fieldsView.table, SWT.NONE );
          item.setText( 1, fieldNames[ i ] );
          item.setText( 2, ValueMetaFactory.getValueMetaName( ValueMetaInterface.TYPE_STRING ) );
        }
      }
      parentDialog.m_fieldsView.removeEmptyRows();
      parentDialog.m_fieldsView.setRowNums();
      parentDialog.m_fieldsView.optWidth( true );


      parentDialog.populateMeta( meta );
    }
  }*/

  public void open( final CommonStepMeta meta, final TransMeta transMeta )  {
    int samples = super.open();
    if ( samples >= 0 ) {

      String message = parentDialog.handleGetFieldsImpl( fieldNames, samples, reloadAll );

      if ( message != null ) {

        if ( reloadAll ) {
          parentDialog.m_fieldsView.table.removeAll();
        } else {
          // TODO: remove
          int dummy = 0;
        }

        // OK, what's the result of our search?
        final List<String> newFieldNames = parentDialog.getNewFields( fieldNames );
        parentDialog.getData( meta, false, newFieldNames, reloadAll );
        parentDialog.m_fieldsView.removeEmptyRows();
        parentDialog.m_fieldsView.setRowNums();
        parentDialog.m_fieldsView.optWidth( true );
        parentDialog.populateMeta( meta );

        EnterTextDialog etd =
          new EnterTextDialog(
            parentDialog.getParent(), BaseMessages.getString( PKG, "CsvInputDialog.ScanResults.DialogTitle" ), BaseMessages
            .getString( PKG, "CsvInputDialog.ScanResults.DialogMessage" ), message, true );
        etd.setReadOnly();
        etd.open();
      }
    }
  }

/*

  protected void handleGetFieldsImpl( final String[] fieldNames, final int samples, boolean reloadAll ) {

    InputStream inputStream = getInputStream();
    try {
      TextFileCSVImportProgressDialog pd =
        new TextFileCSVImportProgressDialog( shell, meta, transMeta, getReader( inputStream ), samples, true );
      String message = pd.open();
      if ( message != null ) {
        // OK, what's the result of our search?
        final List<String> newFieldNames = getNewFields( fieldNames );
        getData( meta, false, newFieldNames, reloadAll );
        m_fieldsView.removeEmptyRows();
        m_fieldsView.setRowNums();
        m_fieldsView.optWidth( true );
        populateMeta( meta );

        EnterTextDialog etd =
          new EnterTextDialog(
            shell, BaseMessages.getString( PKG, "CsvInputDialog.ScanResults.DialogTitle" ), BaseMessages
            .getString( PKG, "CsvInputDialog.ScanResults.DialogMessage" ), message, true );
        etd.setReadOnly();
        etd.open();
      }
    } finally {
      try {
        inputStream.close();
      } catch ( Exception e ) {
        // Ignore close errors
      }
    }
  }*/
}
