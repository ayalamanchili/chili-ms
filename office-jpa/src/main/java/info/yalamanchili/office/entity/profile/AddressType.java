/**
 * Automanage Copyright (C) 2011 yalamanchili.info
 */
package info.yalamanchili.office.entity.profile;

import info.yalamanchili.jpa.AbstractEntity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * @todo add comment for javadoc
 * @author ayalamanchili
 * @generated
 */
@Indexed
@XmlRootElement
@Entity
public class AddressType extends AbstractEntity {

    /**
     * @generated
     */
    @Transient
    private static final long serialVersionUID = 10L;
    /**
     * @generated
     */
    @Field
    @NotEmpty(message = "{addressType.not.empty.msg}")
    protected String addressType;

    /**
     * @generated
     */
    /**
     * @generated
     */
    public AddressType() {
        super();
    }

    /**
     * @generated
     */
    public String getAddressType() {
        return addressType;
    }

    /**
     * @generated
     */
    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }

    /**
     * @generated
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getAddressType());
        sb.append(":");
        return sb.toString();
    }
}
