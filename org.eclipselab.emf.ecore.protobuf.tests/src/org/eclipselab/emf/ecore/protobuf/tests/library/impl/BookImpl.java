/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipselab.emf.ecore.protobuf.tests.library.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipselab.emf.ecore.protobuf.tests.library.Author;
import org.eclipselab.emf.ecore.protobuf.tests.library.Book;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryPackage;
import org.eclipselab.emf.ecore.protobuf.tests.library.Rating;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Book</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipselab.emf.ecore.protobuf.tests.library.impl.BookImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipselab.emf.ecore.protobuf.tests.library.impl.BookImpl#getAuthor <em>Author</em>}</li>
 *   <li>{@link org.eclipselab.emf.ecore.protobuf.tests.library.impl.BookImpl#getRating <em>Rating</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class BookImpl extends EObjectImpl implements Book
{
  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getAuthor() <em>Author</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getAuthor()
   * @generated
   * @ordered
   */
  protected Author author;

  /**
   * The default value of the '{@link #getRating() <em>Rating</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRating()
   * @generated
   * @ordered
   */
  protected static final Rating RATING_EDEFAULT = Rating.NO_RATING;

  /**
   * The cached value of the '{@link #getRating() <em>Rating</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRating()
   * @generated
   * @ordered
   */
  protected Rating rating = RATING_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected BookImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return LibraryPackage.Literals.BOOK;
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
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, LibraryPackage.BOOK__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Author getAuthor()
  {
    if (author != null && author.eIsProxy())
    {
      InternalEObject oldAuthor = (InternalEObject)author;
      author = (Author)eResolveProxy(oldAuthor);
      if (author != oldAuthor)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, LibraryPackage.BOOK__AUTHOR, oldAuthor, author));
      }
    }
    return author;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Author basicGetAuthor()
  {
    return author;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setAuthor(Author newAuthor)
  {
    Author oldAuthor = author;
    author = newAuthor;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, LibraryPackage.BOOK__AUTHOR, oldAuthor, author));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Rating getRating()
  {
    return rating;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRating(Rating newRating)
  {
    Rating oldRating = rating;
    rating = newRating == null ? RATING_EDEFAULT : newRating;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, LibraryPackage.BOOK__RATING, oldRating, rating));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case LibraryPackage.BOOK__NAME:
        return getName();
      case LibraryPackage.BOOK__AUTHOR:
        if (resolve)
          return getAuthor();
        return basicGetAuthor();
      case LibraryPackage.BOOK__RATING:
        return getRating();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case LibraryPackage.BOOK__NAME:
        setName((String)newValue);
        return;
      case LibraryPackage.BOOK__AUTHOR:
        setAuthor((Author)newValue);
        return;
      case LibraryPackage.BOOK__RATING:
        setRating((Rating)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case LibraryPackage.BOOK__NAME:
        setName(NAME_EDEFAULT);
        return;
      case LibraryPackage.BOOK__AUTHOR:
        setAuthor((Author)null);
        return;
      case LibraryPackage.BOOK__RATING:
        setRating(RATING_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case LibraryPackage.BOOK__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case LibraryPackage.BOOK__AUTHOR:
        return author != null;
      case LibraryPackage.BOOK__RATING:
        return rating != RATING_EDEFAULT;
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy())
      return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (name: ");
    result.append(name);
    result.append(", rating: ");
    result.append(rating);
    result.append(')');
    return result.toString();
  }

} //BookImpl
