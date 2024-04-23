package com.keshavarzi.airplanestats.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user", schema = "user_data")
public final class UserEntity implements Cloneable {

  @Id
  @Nonnull
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", table = "user", unique = true, nullable = false)
  private Long userId;

  @Nonnull
  @Column(name = "email", table = "user", unique = true, nullable = false)
  private String email;

  @Nonnull
  @Column(name = "password", table = "user", nullable = false)
  private String password;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @OneToMany(
      mappedBy = "userEntity",
      targetEntity = RouteEntity.class,
      orphanRemoval = true,
      cascade = CascadeType.ALL)
  private Collection<RouteEntity> savedRoutes;

  // Exclusion is because associations impact performance
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @OneToMany(
      mappedBy = "userEntity",
      targetEntity = Trip.class,
      orphanRemoval = true,
      cascade = CascadeType.ALL)
  private Collection<Trip> savedTrips;

  // Do not need to do it on the other entity, this table will be defined for both
  @ManyToMany(fetch = FetchType.EAGER, targetEntity = RoleEntity.class)
  @JoinTable(
      name = "user_role",
      schema = "user_data",
      joinColumns =
          @JoinColumn(
              name = "user_id",
              table = "user_data.role",
              referencedColumnName = "id",
              nullable = false,
              foreignKey = @ForeignKey(name = "user_role_user_id_fk")),
      inverseJoinColumns =
          @JoinColumn(
              name = "role_id",
              table = "role",
              referencedColumnName = "id",
              nullable = false,
              foreignKey = @ForeignKey(name = "user_role_role_id_fk")))
  private Collection<RoleEntity> roleEntities;

  public Collection<RoleEntity> getRoleEntities() throws CloneNotSupportedException {
    return List.copyOf(this.roleEntities);
  }

  /**
   * A deep copy to set roleEntities field. Deep copy is to prevent unintentional/malicious changes
   * through reference
   *
   * @param roleEntities list of role entities
   */
  public void setRoleEntities(final Collection<RoleEntity> roleEntities) {
    this.roleEntities = List.copyOf(roleEntities);
  }

  /**
   * Creates and returns a copy of this object. The precise meaning of "copy" may depend on the
   * class of the object. The general intent is that, for any object {@code x}, the expression:
   *
   * <blockquote>
   *
   * <pre>
   * x.clone() != x</pre>
   *
   * </blockquote>
   *
   * will be true, and that the expression:
   *
   * <blockquote>
   *
   * <pre>
   * x.clone().getClass() == x.getClass()</pre>
   *
   * </blockquote>
   *
   * will be {@code true}, but these are not absolute requirements. While it is typically the case
   * that:
   *
   * <blockquote>
   *
   * <pre>
   * x.clone().equals(x)</pre>
   *
   * </blockquote>
   *
   * will be {@code true}, this is not an absolute requirement.
   *
   * <p>By convention, the returned object should be obtained by calling {@code super.clone}. If a
   * class and all of its superclasses (except {@code Object}) obey this convention, it will be the
   * case that {@code x.clone().getClass() == x.getClass()}.
   *
   * <p>By convention, the object returned by this method should be independent of this object
   * (which is being cloned). To achieve this independence, it may be necessary to modify one or
   * more fields of the object returned by {@code super.clone} before returning it. Typically, this
   * means copying any mutable objects that comprise the internal "deep structure" of the object
   * being cloned and replacing the references to these objects with references to the copies. If a
   * class contains only primitive fields or references to immutable objects, then it is usually the
   * case that no fields in the object returned by {@code super.clone} need to be modified.
   *
   * @return a clone of this instance.
   * @throws CloneNotSupportedException if the object's class does not support the {@code Cloneable}
   *     interface. Subclasses that override the {@code clone} method can also throw this exception
   *     to indicate that an instance cannot be cloned.
   * @implSpec The method {@code clone} for class {@code Object} performs a specific cloning
   *     operation. First, if the class of this object does not implement the interface {@code
   *     Cloneable}, then a {@code CloneNotSupportedException} is thrown. Note that all arrays are
   *     considered to implement the interface {@code Cloneable} and that the return type of the
   *     {@code clone} method of an array type {@code T[]} is {@code T[]} where T is any reference
   *     or primitive type. Otherwise, this method creates a new instance of the class of this
   *     object and initializes all its fields with exactly the contents of the corresponding fields
   *     of this object, as if by assignment; the contents of the fields are not themselves cloned.
   *     Thus, this method performs a "shallow copy" of this object, not a "deep copy" operation.
   *     <p>The class {@code Object} does not itself implement the interface {@code Cloneable}, so
   *     calling the {@code clone} method on an object whose class is {@code Object} will result in
   *     throwing an exception at run time.
   * @see Cloneable
   */
  @Override
  protected UserEntity clone() throws CloneNotSupportedException {
    return (UserEntity) super.clone();
  }
}
