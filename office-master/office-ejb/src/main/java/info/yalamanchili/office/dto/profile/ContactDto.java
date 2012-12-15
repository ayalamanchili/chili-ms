/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.dto.profile;

import info.chili.spring.SpringContext;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import info.yalamanchili.office.entity.profile.Contact;
import info.yalamanchili.office.entity.profile.Email;
import info.yalamanchili.office.entity.profile.Phone;
import info.yalamanchili.office.entity.profile.Sex;
import java.util.Date;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import org.dozer.Mapper;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author raghu
 */
@XmlRootElement(name = "Contact")
@XmlType
public class ContactDto implements Serializable {

    /**
     * firstName
     */
    @NotEmpty(message = "{firstName.not.empty.msg}")
    protected String firstName;
    /**
     * middleInitial
     */
    protected String middleInitial;
    /**
     * lastName
     */
    @NotEmpty(message = "{lastName.not.empty.msg}")
    protected String lastName;
    /**
     * dateOfBirth
     */
    @Past
    @Temporal(javax.persistence.TemporalType.DATE)
    protected Date dateOfBirth;
    /**
     * Sex
     */
    @Enumerated(EnumType.STRING)
    protected Sex sex;
    /**
     * ImageUrl
     */
    protected String imageURL;
    /**
     * email
     */
    @org.hibernate.validator.constraints.Email
    protected String email;
    /**
     * phoneNumber
     */
    @Size(min = 10, max = 10, message = "{phone.phoneNumber.length.invalid.msg}")
    protected String phoneNumber;
    /**
     * phone country code
     */
    @Size(min = 0, max = 4, message = "{phone.countryCode.length.invalid.msg}")
    protected String countryCode;
    /**
     * phone extension
     */
    @Size(min = 0, max = 4, message = "{phone.extension.length.invalid.msg}")
    protected String extension;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleInitial() {
        return middleInitial;
    }

    public void setMiddleInitial(String middleInitial) {
        this.middleInitial = middleInitial;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Sex getSex() {
        return sex;
    }

    public void setSex(Sex sex) {
        this.sex = sex;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @XmlRootElement
    @XmlType
    public static class ContactDtoTable {

        protected Long size;
        protected List<ContactDto> entities;

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        @XmlElement
        public List<ContactDto> getEntities() {
            return entities;
        }

        public void setEntities(List<ContactDto> entities) {
            this.entities = entities;
        }
    }
}
