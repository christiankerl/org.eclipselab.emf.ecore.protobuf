/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package org.eclipselab.emf.ecore.protobuf.tests.library.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipselab.emf.ecore.protobuf.tests.library.Author;
import org.eclipselab.emf.ecore.protobuf.tests.library.Book;
import org.eclipselab.emf.ecore.protobuf.tests.library.Library;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryFactory;
import org.eclipselab.emf.ecore.protobuf.tests.library.LibraryPackage;
import org.eclipselab.emf.ecore.protobuf.tests.library.Rating;


/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class LibraryPackageImpl extends EPackageImpl implements LibraryPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass libraryEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass bookEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass authorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EEnum ratingEEnum = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see org.eclipselab.emf.ecore.protobuf.tests.library.LibraryPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private LibraryPackageImpl()
  {
    super(eNS_URI, LibraryFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link LibraryPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static LibraryPackage init()
  {
    if (isInited)
      return (LibraryPackage)EPackage.Registry.INSTANCE.getEPackage(LibraryPackage.eNS_URI);

    // Obtain or create and register package
    LibraryPackageImpl theLibraryPackage = (LibraryPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof LibraryPackageImpl
      ? EPackage.Registry.INSTANCE.get(eNS_URI) : new LibraryPackageImpl());

    isInited = true;

    // Create package meta-data objects
    theLibraryPackage.createPackageContents();

    // Initialize created meta-data
    theLibraryPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theLibraryPackage.freeze();

    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(LibraryPackage.eNS_URI, theLibraryPackage);
    return theLibraryPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getLibrary()
  {
    return libraryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getLibrary_Name()
  {
    return (EAttribute)libraryEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getLibrary_Authors()
  {
    return (EReference)libraryEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getLibrary_Books()
  {
    return (EReference)libraryEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getBook()
  {
    return bookEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBook_Name()
  {
    return (EAttribute)bookEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getBook_Author()
  {
    return (EReference)bookEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getBook_Rating()
  {
    return (EAttribute)bookEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAuthor()
  {
    return authorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAuthor_Name()
  {
    return (EAttribute)authorEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EEnum getRating()
  {
    return ratingEEnum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LibraryFactory getLibraryFactory()
  {
    return (LibraryFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated)
      return;
    isCreated = true;

    // Create classes and their features
    libraryEClass = createEClass(LIBRARY);
    createEAttribute(libraryEClass, LIBRARY__NAME);
    createEReference(libraryEClass, LIBRARY__AUTHORS);
    createEReference(libraryEClass, LIBRARY__BOOKS);

    bookEClass = createEClass(BOOK);
    createEAttribute(bookEClass, BOOK__NAME);
    createEReference(bookEClass, BOOK__AUTHOR);
    createEAttribute(bookEClass, BOOK__RATING);

    authorEClass = createEClass(AUTHOR);
    createEAttribute(authorEClass, AUTHOR__NAME);

    // Create enums
    ratingEEnum = createEEnum(RATING);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized)
      return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes

    // Initialize classes and features; add operations and parameters
    initEClass(libraryEClass, Library.class, "Library", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(
      getLibrary_Name(),
      ecorePackage.getEString(),
      "name",
      null,
      0,
      1,
      Library.class,
      !IS_TRANSIENT,
      !IS_VOLATILE,
      IS_CHANGEABLE,
      !IS_UNSETTABLE,
      !IS_ID,
      IS_UNIQUE,
      !IS_DERIVED,
      IS_ORDERED);
    initEReference(
      getLibrary_Authors(),
      this.getAuthor(),
      null,
      "authors",
      null,
      0,
      -1,
      Library.class,
      !IS_TRANSIENT,
      !IS_VOLATILE,
      IS_CHANGEABLE,
      IS_COMPOSITE,
      !IS_RESOLVE_PROXIES,
      !IS_UNSETTABLE,
      IS_UNIQUE,
      !IS_DERIVED,
      IS_ORDERED);
    initEReference(
      getLibrary_Books(),
      this.getBook(),
      null,
      "books",
      null,
      0,
      -1,
      Library.class,
      !IS_TRANSIENT,
      !IS_VOLATILE,
      IS_CHANGEABLE,
      IS_COMPOSITE,
      !IS_RESOLVE_PROXIES,
      !IS_UNSETTABLE,
      IS_UNIQUE,
      !IS_DERIVED,
      IS_ORDERED);

    initEClass(bookEClass, Book.class, "Book", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(
      getBook_Name(),
      ecorePackage.getEString(),
      "name",
      null,
      0,
      1,
      Book.class,
      !IS_TRANSIENT,
      !IS_VOLATILE,
      IS_CHANGEABLE,
      !IS_UNSETTABLE,
      !IS_ID,
      IS_UNIQUE,
      !IS_DERIVED,
      IS_ORDERED);
    initEReference(
      getBook_Author(),
      this.getAuthor(),
      null,
      "author",
      null,
      0,
      1,
      Book.class,
      !IS_TRANSIENT,
      !IS_VOLATILE,
      IS_CHANGEABLE,
      !IS_COMPOSITE,
      IS_RESOLVE_PROXIES,
      !IS_UNSETTABLE,
      IS_UNIQUE,
      !IS_DERIVED,
      IS_ORDERED);
    initEAttribute(
      getBook_Rating(),
      this.getRating(),
      "rating",
      null,
      0,
      1,
      Book.class,
      !IS_TRANSIENT,
      !IS_VOLATILE,
      IS_CHANGEABLE,
      !IS_UNSETTABLE,
      !IS_ID,
      IS_UNIQUE,
      !IS_DERIVED,
      IS_ORDERED);

    initEClass(authorEClass, Author.class, "Author", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(
      getAuthor_Name(),
      ecorePackage.getEString(),
      "name",
      null,
      0,
      1,
      Author.class,
      !IS_TRANSIENT,
      !IS_VOLATILE,
      IS_CHANGEABLE,
      !IS_UNSETTABLE,
      !IS_ID,
      IS_UNIQUE,
      !IS_DERIVED,
      IS_ORDERED);

    // Initialize enums and add enum literals
    initEEnum(ratingEEnum, Rating.class, "Rating");
    addEEnumLiteral(ratingEEnum, Rating.NO_RATING);
    addEEnumLiteral(ratingEEnum, Rating.GOOD);
    addEEnumLiteral(ratingEEnum, Rating.MEDIUM);
    addEEnumLiteral(ratingEEnum, Rating.BAD);

    // Create resource
    createResource(eNS_URI);
  }

} //LibraryPackageImpl
