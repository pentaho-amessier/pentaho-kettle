/*! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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

package org.pentaho.di.ui.repository.repositoryexplorer.model;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectory;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.ui.core.PropsUI;
import org.pentaho.di.ui.spoon.Spoon;

import java.util.function.Supplier;

public class UITransformation extends UIRepositoryContent {

  private static final long serialVersionUID = 3826725834758429573L;

  private static final String REPOSITORY_PKG = "org.pentaho.di.ui.repository";

  private Supplier<Spoon> spoonSupplier = Spoon::getInstance;

  public UITransformation() {
  }

  public UITransformation( RepositoryElementMetaInterface rc, UIRepositoryDirectory parent, Repository rep ) {
    super( rc, parent, rep );
  }

  @Override
  public void setName( String name ) throws Exception {
    renameTransformation( this.getObjectId(), getRepositoryDirectory(), name );
    super.setName( name );
    uiParent.fireCollectionChanged();
  }

  protected ObjectId renameTransformation( ObjectId objectId, RepositoryDirectory directory, String name )
    throws Exception {
    String comment = BaseMessages.getString( REPOSITORY_PKG, "Repository.Rename", super.getName(), name );
    return rep.renameTransformation( this.getObjectId(), comment, getRepositoryDirectory(), name );
  }

  public void delete() throws Exception {
    PropsUI.removeRecent( getRepository(), getId(), getType().toLowerCase() );
    getSpoon().getDisplay().asyncExec( () -> {
      getSpoon().addMenuLast();
    } );
    rep.deleteTransformation( this.getObjectId() );
    if ( uiParent.getRepositoryObjects().contains( this ) ) {
      uiParent.getRepositoryObjects().remove( this );
    }
  }

  public void move( UIRepositoryDirectory newParentDir ) throws KettleException {
    if ( newParentDir != null ) {
      rep.renameTransformation( obj.getObjectId(), newParentDir.getDirectory(), null );
      newParentDir.refresh();
    }
  }

  @Override
  public String getImage() {
    return "ui/images/transrepo.svg";
  }

  private Spoon getSpoon() {
    return spoonSupplier.get();
  }
}
