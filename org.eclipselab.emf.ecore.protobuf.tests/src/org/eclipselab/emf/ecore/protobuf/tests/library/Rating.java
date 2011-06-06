/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipselab.emf.ecore.protobuf.tests.library;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.Enumerator;


/**
 * <!-- begin-user-doc -->
 * A representation of the literals of the enumeration '<em><b>Rating</b></em>',
 * and utility methods for working with them.
 * <!-- end-user-doc -->
 * @see org.eclipselab.emf.ecore.protobuf.tests.library.LibraryPackage#getRating()
 * @model
 * @generated
 */
public enum Rating implements Enumerator {
  /**
   * The '<em><b>NO RATING</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #NO_RATING_VALUE
   * @generated
   * @ordered
   */
  NO_RATING(0, "NO_RATING", "NO_RATING"),

  /**
   * The '<em><b>GOOD</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #GOOD_VALUE
   * @generated
   * @ordered
   */
  GOOD(1, "GOOD", "GOOD"),

  /**
   * The '<em><b>MEDIUM</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #MEDIUM_VALUE
   * @generated
   * @ordered
   */
  MEDIUM(2, "MEDIUM", "MEDIUM"),

  /**
   * The '<em><b>BAD</b></em>' literal object.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #BAD_VALUE
   * @generated
   * @ordered
   */
  BAD(3, "BAD", "BAD");

  /**
   * The '<em><b>NO RATING</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>NO RATING</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #NO_RATING
   * @model
   * @generated
   * @ordered
   */
  public static final int NO_RATING_VALUE = 0;

  /**
   * The '<em><b>GOOD</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>GOOD</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #GOOD
   * @model
   * @generated
   * @ordered
   */
  public static final int GOOD_VALUE = 1;

  /**
   * The '<em><b>MEDIUM</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>MEDIUM</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #MEDIUM
   * @model
   * @generated
   * @ordered
   */
  public static final int MEDIUM_VALUE = 2;

  /**
   * The '<em><b>BAD</b></em>' literal value.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of '<em><b>BAD</b></em>' literal object isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @see #BAD
   * @model
   * @generated
   * @ordered
   */
  public static final int BAD_VALUE = 3;

  /**
   * An array of all the '<em><b>Rating</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static final Rating[] VALUES_ARRAY = new Rating []{ NO_RATING, GOOD, MEDIUM, BAD, };

  /**
   * A public read-only list of all the '<em><b>Rating</b></em>' enumerators.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static final List<Rating> VALUES = Collections.unmodifiableList(Arrays.asList(VALUES_ARRAY));

  /**
   * Returns the '<em><b>Rating</b></em>' literal with the specified literal value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static Rating get(String literal)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      Rating result = VALUES_ARRAY[i];
      if (result.toString().equals(literal))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Rating</b></em>' literal with the specified name.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static Rating getByName(String name)
  {
    for (int i = 0; i < VALUES_ARRAY.length; ++i)
    {
      Rating result = VALUES_ARRAY[i];
      if (result.getName().equals(name))
      {
        return result;
      }
    }
    return null;
  }

  /**
   * Returns the '<em><b>Rating</b></em>' literal with the specified integer value.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static Rating get(int value)
  {
    switch (value)
    {
      case NO_RATING_VALUE:
        return NO_RATING;
      case GOOD_VALUE:
        return GOOD;
      case MEDIUM_VALUE:
        return MEDIUM;
      case BAD_VALUE:
        return BAD;
    }
    return null;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final int value;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final String name;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private final String literal;

  /**
   * Only this class can construct instances.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private Rating(int value, String name, String literal)
  {
    this.value = value;
    this.name = name;
    this.literal = literal;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public int getValue()
  {
    return value;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getLiteral()
  {
    return literal;
  }

  /**
   * Returns the literal value of the enumerator, which is its string representation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    return literal;
  }

} //Rating
