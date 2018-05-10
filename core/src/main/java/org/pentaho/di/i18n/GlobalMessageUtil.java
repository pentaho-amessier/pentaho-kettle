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

package org.pentaho.di.i18n;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.Set;

public class GlobalMessageUtil {

  private static final Logger log = LoggerFactory.getLogger( GlobalMessageUtil.class );

  /**
   * Used when the preferred locale (as defined by the user) is not available.
   */
  public static final Locale FAILOVER_LOCALE = Locale.US;

  protected static final LanguageChoice langChoice = LanguageChoice.getInstance();

  protected static final ThreadLocal<Locale> threadLocales = new ThreadLocal<Locale>();

  protected static final Map<String, ResourceBundle> locales = Collections
    .synchronizedMap( new HashMap<String, ResourceBundle>() );

  public static String formatErrorMessage( String key, String msg ) {
    String s2 = key.substring( 0, key.indexOf( '.' ) + "ERROR_0000".length() + 1 );
    return BaseMessages.getString( "MESSUTIL.ERROR_FORMAT_MASK", s2, msg );
  }

  public static String getString( ResourceBundle bundle, String key ) throws MissingResourceException {
    return MessageFormat.format( bundle.getString( key ), new Object[] {} );
  }

  public static String getErrorString( ResourceBundle bundle, String key ) {
    return formatErrorMessage( key, getString( bundle, key ) );
  }

  public static String getString( ResourceBundle bundle, String key, String param1 ) {
    try {
      Object[] args = { param1 };
      return MessageFormat.format( bundle.getString( key ), args );
    } catch ( Exception e ) {
      return decorateMissingKey( key );
    }
  }

  public static String getErrorString( ResourceBundle bundle, String key, String param1 ) {
    return formatErrorMessage( key, getString( bundle, key, param1 ) );
  }

  public static String getString( ResourceBundle bundle, String key, String param1, String param2 ) {
    try {
      Object[] args = { param1, param2 };
      return MessageFormat.format( bundle.getString( key ), args );
    } catch ( Exception e ) {
      return decorateMissingKey( key );
    }
  }

  public static String getErrorString( ResourceBundle bundle, String key, String param1, String param2 ) {
    return formatErrorMessage( key, getString( bundle, key, param1, param2 ) );
  }

  public static String getString( ResourceBundle bundle, String key, String param1, String param2, String param3 ) {
    try {
      Object[] args = { param1, param2, param3 };
      return MessageFormat.format( bundle.getString( key ), args );
    } catch ( Exception e ) {
      return decorateMissingKey( key );
    }
  }

  public static String getErrorString( ResourceBundle bundle, String key, String param1, String param2,
                                String param3 ) {
    return formatErrorMessage( key, getString( bundle, key, param1, param2, param3 ) );
  }

  public static String getString( ResourceBundle bundle, String key, String param1, String param2, String param3,
                           String param4 ) {
    try {
      Object[] args = { param1, param2, param3, param4 };
      return MessageFormat.format( bundle.getString( key ), args );
    } catch ( Exception e ) {
      return decorateMissingKey( key );
    }
  }

  public static String getString( ResourceBundle bundle, String key, String param1, String param2, String param3,
                           String param4, String param5 ) {
    try {
      Object[] args = { param1, param2, param3, param4, param5 };
      return MessageFormat.format( bundle.getString( key ), args );
    } catch ( Exception e ) {
      return decorateMissingKey( key );
    }
  }

  public static String getString( ResourceBundle bundle, String key, String param1, String param2, String param3,
                           String param4, String param5, String param6 ) {
    try {
      Object[] args = { param1, param2, param3, param4, param5, param6 };
      return MessageFormat.format( bundle.getString( key ), args );
    } catch ( Exception e ) {
      return decorateMissingKey( key );
    }
  }

  public static String getErrorString( ResourceBundle bundle, String key, String param1, String param2,
                                String param3, String param4 ) {
    return formatErrorMessage( key, getString( bundle, key, param1, param2, param3, param4 ) );
  }

  public static synchronized void setLocale( Locale newLocale ) {
    threadLocales.set( newLocale );
  }

  public static synchronized Locale getLocale() {
    Locale rtn = threadLocales.get();
    if ( rtn != null ) {
      return rtn;
    }

    setLocale( langChoice.getDefaultLocale() );
    return langChoice.getDefaultLocale();
  }

  /**
   * Returns a {@link Set} of locales for consideration when translating text. The {@link Set} contains the user
   * selected preferred locale, the failover locale (english) and the "empty" locale, as well as their language-only (no
   * country qualifier) equivalents.
   *
   * @return Returns a {@link Set} of locales for consideration when translating text
   */
  public static Set<Locale> getActiveLocales() {
    // Example: messages_fr_FR.properties
    final Locale defaultLocale = langChoice.getDefaultLocale();
    // Example: messages_en_US.properties
    final Locale failoverLocale = FAILOVER_LOCALE;
    // Example: messages.properties
    final Locale noLangLocale = Locale.ROOT;

    LinkedHashSet<Locale> activeLocales = new LinkedHashSet();
    activeLocales.add( defaultLocale );
    activeLocales.add( failoverLocale );
    activeLocales.add( noLangLocale );
    return activeLocales;
  }
  /**
   * Calls {@link #calculateString(String[], String, Object[], Class, String, boolean)} with the {@code
   * logNotFoundError} parameter set to {@code true} to ensure proper error logging when the localized string cannot be
   * found.
   */
  public static String calculateString(
    String[] pkgNames, String key, Object[] parameters, Class<?> resourceClass, final String bundleName ) {
    return calculateString( pkgNames, key, parameters, resourceClass, bundleName, true );
  }

  /**
   * Returns the localized string for the given {@code key} and {@code parameters} in a bundle defined by the the
   * concatenation of the package names defined in {@code packageName} and @code bundleName} (the first valid
   * combination of {@code packageName} + {@code bundleName} wins), sing the provided {@code resourceClass}'s class
   * loader.
   *
   * @param pkgNames         an array of packages potentially containing the localized messages the first one found to
   *                         contain the messages is the one that is used to localize the message
   * @param key              the message key being looked up
   * @param parameters       parameters within the looked up message
   * @param resourceClass    the class whose class loader is used to getch the resource bundle
   * @param bundleName       the name of the message bundle
   * @param logNotFoundError determines whether an error is logged when the localized string cannot be found - it can be
   *                         used to suppress the log in cases where it is known that various combinations of parameters
   *                         will be tried to fetch the message, to avoid unnecessary error logging.
   * @return the localized string for the given {@code key} and {@code parameters} in a bundle defined by the the
   * concatenation of the package names defined in {@code packageName} and @code bundleName} (the first valid
   * combination of {@code packageName} + @code bundleName} wins), sing the provided {@code resourceClass}'s class
   * loader
   */
  public static String calculateString(
    String[] pkgNames, String key, Object[] parameters, Class<?> resourceClass, final String bundleName,
    final boolean logNotFoundError ) {

    final Map<Locale, String> potentialMatches = new HashMap<>();
    for ( final String pkgName : pkgNames ) {
      final Map<Locale, String> strings = calculateString( pkgName, getActiveLocales(), key, parameters,
        resourceClass, bundleName );
      potentialMatches.putAll( strings );
    }
    if ( potentialMatches.isEmpty() && logNotFoundError ) {
      String message =
        "Message not found in the preferred and failover locale: key=[" + key + "], package=" + Arrays
          .asList( pkgNames );
      log.error( Const.getStackTracker( new KettleException( message ) ) );
    }
    return getBestMatch( key, getActiveLocales(), potentialMatches );
  }

  @VisibleForTesting
  static Map<Locale, String> calculateString( String packageName, Set<Locale> locales, String key, Object[] parameters,
                                       Class<?> resourceClass, final String bundleName ) {
    final Map<Locale, String> potentialMatches = new HashMap<>();
    for ( final Locale locale : locales ) {
      final String string = calculateString( packageName, locale, key, parameters, resourceClass, bundleName );
      if ( !isMissingKey( string ) ) {
        potentialMatches.put( locale, string );
      }
    }
    return potentialMatches;
  }

  private static String decorateMissingKey( final String key ) {
    final StringBuilder missingKey = new StringBuilder();
    missingKey.append( "!" ).append( key ).append( "!" );
    return missingKey.toString();
  }

  /**
   * Of all the matches found, returns the one that is most preferable, according to the order of the locale set, which
   * is expected to be ordered.
   *
   * @param key
   * @param locales
   * @param potentialMatches
   * @return the string with the most preferred locale, of all the available locales.
   */
  private static String getBestMatch( final String key, final Set<Locale> locales, final Map<Locale, String>
    potentialMatches ) {
    if ( potentialMatches != null && !potentialMatches.isEmpty() ) {
      for ( final Locale locale : locales ) {
        final String string = potentialMatches.get( locale );
        if ( string != null ) {
          return string;
        }
      }
    }
    return decorateMissingKey( key );
  }

  @VisibleForTesting
  static String calculateString( String packageName, Locale locale, String key, Object[] parameters,
                          Class<?> resourceClass, final String bundleName ) {
    ResourceBundle bundle = getBundle( locale, packageName + "." + bundleName, resourceClass );
    if ( bundle != null ) {
      String unformattedString = null;
      try {
        unformattedString = bundle.getString( key );
        String string = MessageFormat.format( unformattedString, parameters );
        return string;
      } catch ( final MissingResourceException e ) {
        // no-op
      }
    }
    return "";
  }

  /**
   * Returns a resource bundle of the default or fail-over locales, or null, if the bundle cannot be found.
   *
   * @param packagePath   The package to search in
   * @param resourceClass the class to use to resolve the bundle
   * @return The resource bundle, or null, if the bundle cannot be found
   */
  public static ResourceBundle getBundle( String packagePath, Class<?> resourceClass ) {
    ResourceBundle bundle = null;
    for ( final Locale locale : getActiveLocales() ) {
      final StringBuilder bundleKeyBldr = new StringBuilder();
      bundleKeyBldr.append( packagePath ).append( "." ).append( locale ).append( "." )
        .append( resourceClass.getName() );
      final String bundleKey = bundleKeyBldr.toString();
      bundle = locales.get( bundleKey );
      if ( bundle == null ) {
        bundle = getBundle( locale, packagePath, resourceClass, false );
        locales.put( bundleKey, bundle );
      }
      if ( bundle != null ) {
        break;
      }
    }
    return bundle;
  }


  public static ResourceBundle getBundle( Locale locale, String packagePath, Class<?> resourceClass ) {
    return getBundle( locale, packagePath, resourceClass, true );
  }

  public static ResourceBundle getBundle( Locale locale, String packagePath, Class<?> resourceClass, boolean
    fallbackOnRoot ) {
    ResourceBundle bundle = null;
    final StringBuilder bundleKeyBldr = new StringBuilder();
    bundleKeyBldr.append( packagePath ).append( "." ).append( locale ).append( "." ).append( resourceClass.getName() );
    final String bundleKey = bundleKeyBldr.toString();
    bundle = locales.get( bundleKey );
    if ( bundle == null ) {
      try {
        bundle = ResourceBundle.getBundle( packagePath, locale, resourceClass.getClassLoader(),
          new ResourceBundle.Control() {

            /** Overridden to return null, since we have our own fall-back mechanism */
            @Override
            public Locale getFallbackLocale( String baseName, Locale locale ) {
              // we have our own fall-back mechanism
              return null;
            }

            /** Overridden to remove the ROOT locale, when appropriate, since we have our own way of handling it **/
            @Override
            public List<Locale> getCandidateLocales( String baseName, Locale locale ) {
              // we have our own fall-back mechanism
              final List<Locale> locales = super.getCandidateLocales( baseName, locale );
              // remove the root locale, as we want to handle it ourselves
              if ( !fallbackOnRoot && !locale.equals( Locale.ROOT ) ) {
                locales.remove( Locale.ROOT );
              }
              return locales;
            }

            /** Overridden to use UTF_8 encoding **/
            @Override
            public ResourceBundle newBundle( final String baseName, final Locale locale, final String format,
                                             final ClassLoader loader, final boolean reload )
              throws IllegalAccessException, InstantiationException, IOException {
              final String bundleName = toBundleName( baseName, locale );
              final String resourceName = toResourceName( bundleName, "properties" );
              ResourceBundle bundle = locales.get( resourceName );
              if ( bundle != null ) {
                return bundle;
              }
              InputStream stream = null;
              if ( reload ) {
                final URL url = loader.getResource( resourceName );
                if ( url != null ) {
                  final URLConnection connection = url.openConnection();
                  if ( connection != null ) {
                    connection.setUseCaches( false );
                    stream = connection.getInputStream();
                  }
                }
              } else {
                stream = loader.getResourceAsStream( resourceName );
              }
              if ( stream != null ) {
                try {
                  bundle = new PropertyResourceBundle( new InputStreamReader( stream, "UTF-8" ) );
                } finally {
                  stream.close();
                }
              }
              locales.put( resourceName, bundle );
              return bundle;
            }

          } );
        locales.put( bundleKey, bundle );
      } catch ( final MissingResourceException e ) {
        // no-op
      }
    }
    return bundle;
  }

  /**
   * Returns a string corresponding to the locale (Example: "en", "en_US").
   *
   * @param locale The {@link Locale} whose string representation it being returned
   * @return a string corresponding to the locale (Example: "en", "en_US").
   */
  protected static String getLocaleString( Locale locale ) {
    final StringBuilder localeString = new StringBuilder();
    if ( locale != null && !StringUtils.isBlank( locale.getLanguage() ) ) {
      if ( !StringUtils.isBlank( locale.getCountry() ) ) {
        // force language to be lower case and country to be upper case
        localeString.append( locale.getLanguage().toLowerCase() ).append( "_" ).append( locale.getCountry()
          .toUpperCase() );
      } else {
        // force language to be lower case
        localeString.append( locale.getLanguage().toLowerCase() );
      }
    }
    return localeString.toString();
  }

  /**
   * Returns the full path to the message bundle defned by the {@code packagePath} and {@link Locale}.
   *
   * @param locale      the {@link Locale} used to contruct the key
   * @param packagePath the full path to the message file without the extension
   * @return the full path to the message bundle defned by the {@code packagePath} and {@link Locale}
   */
  protected static String buildHashKey( Locale locale, String packagePath ) {
    final String localeString = getLocaleString( locale );
    if ( StringUtils.isBlank( localeString ) ) {
      return Const.NVL( packagePath, "" );
    } else {
      if ( StringUtils.isBlank( packagePath ) ) {
        return localeString;
      } else {
        final StringBuilder key = new StringBuilder();
        key.append( packagePath ).append( "_" ).append( localeString );
        return key.toString();
      }
    }
  }

  /**
   * Returns true if the given {@code string} is null or is in the format of a missing key: !key!.
   *
   * @param string
   * @return true if the given {@code string} is null or is in the format of a missing key: !key!.
   */
  protected static boolean isMissingKey( final String string ) {
    return StringUtils.isBlank( string ) || ( string.trim().startsWith( "!" ) && string.trim().endsWith( "!" )
      && !string.trim().equals( "!" ) );
  }
}
