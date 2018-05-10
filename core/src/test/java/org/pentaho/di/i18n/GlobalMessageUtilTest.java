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

package org.pentaho.di.i18n;

import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;
import java.util.ResourceBundle;

public class GlobalMessageUtilTest {

  @Test
  public void testGetLocaleString() {
    Assert.assertEquals( "", GlobalMessageUtil.getInstance().getLocaleString( null ) );
    Assert.assertEquals( "", GlobalMessageUtil.getInstance().getLocaleString( new Locale( "" ) ) );
    Assert.assertEquals( "en", GlobalMessageUtil.getInstance().getLocaleString( Locale.ENGLISH ) );
    Assert.assertEquals( "en_US", GlobalMessageUtil.getInstance().getLocaleString( Locale.US ) );
    Assert.assertEquals( "en", GlobalMessageUtil.getInstance().getLocaleString( new Locale( "EN" ) ) );
    Assert.assertEquals( "en_US", GlobalMessageUtil.getInstance().getLocaleString( new Locale( "EN", "us" ) ) );
  }

  @Test
  public void testBuildHashKey() {
    Assert.assertEquals( "", GlobalMessageUtil.getInstance().buildHashKey( null, null ) );
    Assert.assertEquals( "", GlobalMessageUtil.getInstance().buildHashKey( null, "" ) );
    Assert.assertEquals( "foo", GlobalMessageUtil.getInstance().buildHashKey( null, "foo" ) );
    Assert.assertEquals( "", GlobalMessageUtil.getInstance().buildHashKey( new Locale( "" ), null ) );
    Assert.assertEquals( "en", GlobalMessageUtil.getInstance().buildHashKey( Locale.ENGLISH, null ) );
    Assert.assertEquals( "en", GlobalMessageUtil.getInstance().buildHashKey( Locale.ENGLISH, "" ) );
    Assert.assertEquals( "en_US", GlobalMessageUtil.getInstance().buildHashKey( Locale.US, null ) );
    Assert.assertEquals( "en_US", GlobalMessageUtil.getInstance().buildHashKey( Locale.US, "" ) );
    Assert.assertEquals( "foo_en_US", GlobalMessageUtil.getInstance().buildHashKey( new Locale( "EN", "us" ), "foo" ) );
  }

  @Test
  public void isMissingKey() {
    Assert.assertTrue( GlobalMessageUtil.getInstance().isMissingKey( null ) );
    Assert.assertTrue( GlobalMessageUtil.getInstance().isMissingKey( "" ) );
    Assert.assertTrue( GlobalMessageUtil.getInstance().isMissingKey( " " ) );
    Assert.assertTrue( GlobalMessageUtil.getInstance().isMissingKey( "!foo!" ) );
    Assert.assertTrue( GlobalMessageUtil.getInstance().isMissingKey( "!foo! " ) );
    Assert.assertTrue( GlobalMessageUtil.getInstance().isMissingKey( " !foo!" ) );
    Assert.assertFalse( GlobalMessageUtil.getInstance().isMissingKey( "!foo" ) );
    Assert.assertFalse( GlobalMessageUtil.getInstance().isMissingKey( "foo!" ) );
    Assert.assertFalse( GlobalMessageUtil.getInstance().isMissingKey( "foo" ) );
    Assert.assertFalse( GlobalMessageUtil.getInstance().isMissingKey( "!" ) );
    Assert.assertFalse( GlobalMessageUtil.getInstance().isMissingKey( " !" ) );
  }

  @Test
  public void calculateString() {

    // "fr", "FR"
    Assert.assertEquals( "Une certaine valeur foo", GlobalMessageUtil.getInstance().calculateString(
      GlobalMessages.SYSTEM_BUNDLE_PACKAGE, Locale.FRANCE, "someKey", new String[] { "foo" }, GlobalMessages.PKG,
      GlobalMessages.BUNDLE_NAME ) );

    // "fr" - should fall back on default bundle
    String str = GlobalMessageUtil.getInstance().calculateString( GlobalMessages.SYSTEM_BUNDLE_PACKAGE,
        Locale.FRENCH, "someKey", new String[] { "foo" }, GlobalMessages.PKG, GlobalMessages.BUNDLE_NAME );
    Assert.assertEquals( "Some Value foo", str );

    // "jp"
    Assert.assertEquals( "何らかの値 foo", GlobalMessageUtil.getInstance().calculateString( GlobalMessages.SYSTEM_BUNDLE_PACKAGE,
      Locale.JAPANESE, "someKey", new String[] { "foo" }, GlobalMessages.PKG, GlobalMessages.BUNDLE_NAME ) );

    // "jp", "JP" - should fall back on "jp"
    str = GlobalMessageUtil.getInstance().calculateString( GlobalMessages.SYSTEM_BUNDLE_PACKAGE, Locale.JAPAN, "someKey", new String[]
        { "foo" }, GlobalMessages.PKG, GlobalMessages.BUNDLE_NAME );
    Assert.assertEquals( "何らかの値 foo", str );

    // try with multiple packages
    // make sure the selected language is used correctly
    LanguageChoice.getInstance().setDefaultLocale( Locale.FRANCE ); // "fr", "FR"
    Assert.assertEquals( "Une certaine valeur foo", GlobalMessageUtil.getInstance().calculateString( new String[] { GlobalMessages
      .SYSTEM_BUNDLE_PACKAGE }, "someKey", new String[] { "foo" }, GlobalMessages.PKG, GlobalMessages.BUNDLE_NAME ) );

    LanguageChoice.getInstance().setDefaultLocale( Locale.FRENCH ); // "fr" - fall back on "default" messages.properties
    Assert.assertEquals( "Some Value foo", GlobalMessageUtil.getInstance().calculateString( new String[] { GlobalMessages
      .SYSTEM_BUNDLE_PACKAGE }, "someKey", new String[] { "foo" }, GlobalMessages.PKG, GlobalMessages.BUNDLE_NAME ) );

    LanguageChoice.getInstance().setDefaultLocale( Locale.FRENCH ); // "fr" - fall back on foo/messages_fr.properties
    Assert.assertEquals( "Une certaine valeur foo", GlobalMessageUtil.getInstance().calculateString( new String[] { GlobalMessages
        .SYSTEM_BUNDLE_PACKAGE, "org.pentaho.di.foo" }, "someKey", new String[] { "foo" }, GlobalMessages.PKG,
      GlobalMessages.BUNDLE_NAME ) );

    LanguageChoice.getInstance().setDefaultLocale( Locale.JAPANESE ); // "jp"
    Assert.assertEquals( "何らかの値 foo", GlobalMessageUtil.getInstance().calculateString( new String[] { GlobalMessages
      .SYSTEM_BUNDLE_PACKAGE }, "someKey", new String[] { "foo" }, GlobalMessages.PKG, GlobalMessages.BUNDLE_NAME ) );

    LanguageChoice.getInstance().setDefaultLocale( Locale.JAPAN ); // "jp", "JP" - fall back on "jp"
    Assert.assertEquals( "何らかの値 foo", GlobalMessageUtil.getInstance().calculateString( new String[] { GlobalMessages
      .SYSTEM_BUNDLE_PACKAGE }, "someKey", new String[] { "foo" }, GlobalMessages.PKG, GlobalMessages.BUNDLE_NAME ) );
  }
}
