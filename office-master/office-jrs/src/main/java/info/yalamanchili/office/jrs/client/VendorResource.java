/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.jrs.client;

import info.chili.commons.SearchUtils;
import info.chili.service.jrs.exception.ServiceException;
import info.chili.service.jrs.types.Entry;
import info.chili.dao.CRUDDao;
import info.chili.jpa.validation.Validate;
import info.yalamanchili.office.cache.OfficeCacheKeys;
import info.yalamanchili.office.dao.client.VendorDao;
import info.yalamanchili.office.dao.profile.AddressDao;
import info.yalamanchili.office.dao.profile.CompanyDao;
import info.yalamanchili.office.dao.profile.ContactDao;
import info.yalamanchili.office.dao.profile.EmployeeDao;
import info.yalamanchili.office.dao.security.OfficeSecurityService;
import info.yalamanchili.office.dto.client.ContractSearchDto;
import info.yalamanchili.office.dto.profile.ContactDto;
import info.yalamanchili.office.dto.profile.ContactDto.ContactDtoTable;
import info.yalamanchili.office.dto.profile.EmployeeSearchDto;
import info.yalamanchili.office.entity.client.Vendor;
import info.yalamanchili.office.entity.profile.Address;
import info.yalamanchili.office.entity.profile.ClientInformation;
import info.yalamanchili.office.entity.profile.Contact;
import info.yalamanchili.office.entity.profile.Employee;
import info.yalamanchili.office.jrs.CRUDResource;
import info.yalamanchili.office.jrs.profile.AddressResource.AddressTable;
import info.yalamanchili.office.mapper.profile.ContactMapper;
import info.yalamanchili.office.profile.ContactService;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.apache.commons.lang.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Prashanthi
 */
@Path("secured/vendor")
@Component
@Scope("request")
public class VendorResource extends CRUDResource<Vendor> {

    @Autowired
    public EmployeeDao employeeDao;
    @Autowired
    public VendorDao vendorDao;
    @Autowired
    protected ContactService contactService;
    @PersistenceContext
    protected EntityManager em;
    @Autowired
    protected Mapper mapper;

    @Override
    public CRUDDao getDao() {
        return vendorDao;
    }

    @GET
    @Path("/{start}/{limit}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CONTRACTS','ROLE_BILLING_AND_INVOICING','ROLE_CONTRACTS_FULL_VIEW')")
    @Cacheable(OfficeCacheKeys.VENDOR)
    public VendorTable table(@PathParam("start") int start, @PathParam("limit") int limit) {
        VendorTable tableObj = new VendorTable();
        tableObj.setEntities(getDao().query(start, limit));
        tableObj.setSize(getDao().size());
        return tableObj;
    }

    @PUT
    @Validate
    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CONTRACTS_ADMIN','ROLE_BILLING_AND_INVOICING')")
    @CacheEvict(value = OfficeCacheKeys.VENDOR, allEntries = true)
    public Vendor save(Vendor vendor) {
        return super.save(vendor);
    }

    @PUT
    @Path("/delete/{id}")
    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CONTRACTS_ADMIN','ROLE_BILLING_AND_INVOICING')")
    @CacheEvict(value = OfficeCacheKeys.VENDOR, allEntries = true)
    public void delete(@PathParam("id") Long id) {
        super.delete(id);
    }

    @GET
    @Path("/dropdown/{start}/{limit}")
    @Transactional(propagation = Propagation.NEVER)
    @Cacheable(OfficeCacheKeys.VENDOR)
    @Override
    public List<Entry> getDropDown(@PathParam("start") int start, @PathParam("limit") int limit,
            @QueryParam("column") List<String> columns) {
        return super.getDropDown(start, limit, columns);
    }

    /**
     * Add Vendor Contact
     *
     * @param vendorId
     * @param dto
     *
     */
    @PUT
    @Validate
    @Path("/vendorcontact/{vendorId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CONTRACTS_ADMIN','ROLE_BILLING_AND_INVOICING')")
    public void addvendorContact(@PathParam("vendorId") Long vendorId, ContactDto dto) {
        Vendor vendor = (Vendor) getDao().findById(vendorId);
        Contact contact = contactService.save(dto);
        vendor.addContact(contact);
    }

    @PUT
    @Validate
    @Path("/acct-pay-contact/{vendorId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CONTRACTS_ADMIN','ROLE_BILLING_AND_INVOICING')")
    public void addvendorAcctPayContact(@PathParam("vendorId") Long vendorId, ContactDto dto) {
        Vendor vendor = (Vendor) getDao().findById(vendorId);
        Contact contact = contactService.save(dto);
        vendor.addAcctPayContact(contact);
    }

    @PUT
    @Path("/contact/remove/{vendorId}/{contactId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CONTRACTS_ADMIN','ROLE_BILLING_AND_INVOICING')")
    public void removeContact(@PathParam("vendorId") Long vendorId, @PathParam("contactId") Long contactId) {
        Vendor vendor = (Vendor) getDao().findById(vendorId);
        Contact contact = ContactDao.instance().findById(contactId);
        vendor.getContacts().remove(contact);
        ContactDao.instance().delete(contact.getId());
    }

    @PUT
    @Path("/acct-pay-contact/remove/{vendorId}/{contactId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CONTRACTS_ADMIN','ROLE_BILLING_AND_INVOICING')")
    public void removeAcctPayContact(@PathParam("vendorId") Long vendorId, @PathParam("contactId") Long contactId) {
        Vendor vendor = (Vendor) getDao().findById(vendorId);
        Contact contact = ContactDao.instance().findById(contactId);
        vendor.getAcctPayContacts().remove(contact);
        ContactDao.instance().delete(contact.getId());
    }

    /**
     * Get Vendor Contact
     *
     * @param id
     * @param start
     * @param limit
     * @return
     *
     */
    @GET
    @Path("/vendorcontact/{id}/{start}/{limit}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CONTRACTS','ROLE_BILLING_AND_INVOICING','ROLE_CONTRACTS_FULL_VIEW')")
    public ContactDtoTable getVendorContacts(@PathParam("id") long id, @PathParam("start") int start,
            @PathParam("limit") int limit) {
        Vendor vendor = (Vendor) getDao().findById(id);
        return getContacts(vendor.getContacts());
    }

    /**
     * Get Vendor Account Payable Contact
     *
     * @param id
     * @param start
     * @param limit
     * @return
     *
     */
    @GET
    @Path("/acct-pay-contacts/{id}/{start}/{limit}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CONTRACTS','ROLE_BILLING_AND_INVOICING','ROLE_CONTRACTS_FULL_VIEW')")
    public ContactDtoTable getVendorAcctPayContacts(@PathParam("id") long id, @PathParam("start") int start,
            @PathParam("limit") int limit) {
        Vendor vendor = (Vendor) getDao().findById(id);
        return getContacts(vendor.getAcctPayContacts());
    }

    protected ContactDtoTable getContacts(List<Contact> contacts) {
        ContactDtoTable tableObj = new ContactDtoTable();
        for (Contact entity : contacts) {
            tableObj.getEntities().add(ContactMapper.map(entity));
        }
        tableObj.setSize((long) contacts.size());
        return tableObj;
    }

    @GET
    @Path("/contacts/dropdown/{id}/{start}/{limit}")
    public List<Entry> getVendorContactsDropDown(@PathParam("id") long id, @PathParam("start") int start, @PathParam("limit") int limit,
            @QueryParam("column") List<String> columns) {
        Vendor vendor = VendorDao.instance().findById(id);
        return getContactDropDown(vendor.getContacts());
    }

    @GET
    @Path("/acct-pay-contacts/dropdown/{id}/{start}/{limit}")
    public List<Entry> getVendorAcctPayContactsDropDown(@PathParam("id") long id, @PathParam("start") int start, @PathParam("limit") int limit,
            @QueryParam("column") List<String> columns) {
        Vendor vendor = VendorDao.instance().findById(id);
        return getContactDropDown(vendor.getAcctPayContacts());
    }

    protected List<Entry> getContactDropDown(List<Contact> contacts) {
        List<Entry> result = new ArrayList<>();
        for (Contact contact : contacts) {
            Entry entry = new Entry();
            entry.setId(contact.getId().toString());
            entry.setValue(contact.getFirstName() + " " + contact.getLastName());
            result.add(entry);
        }
        return result;
    }

    /**
     * Add Vendor locations
     *
     * @param vendorId
     * @param address
     *
     */
    @PUT
    @Validate
    @Path("/vendorlocation/{vendorId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CONTRACTS_ADMIN','ROLE_BILLING_AND_INVOICING')")
    public void addvendorlocation(@PathParam("vendorId") Long vendorId, Address address) {
        Vendor vend = (Vendor) getDao().findById(vendorId);
        vend.addLocations(address);
    }

    @PUT
    @Path("/location/remove/{vendorId}/{locationId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CONTRACTS_ADMIN','ROLE_BILLING_AND_INVOICING')")
    public void removeLocation(@PathParam("vendorId") Long vendorId, @PathParam("locationId") Long locationId) {
        Vendor vendor = (Vendor) getDao().findById(vendorId);
        if (vendor == null) {
            throw new ServiceException(ServiceException.StatusCode.INVALID_REQUEST, "DELETE", "vendorIdInvalid", "vendor not found");
        }
        Address location = AddressDao.instance().findById(locationId);
        if (location == null) {
            throw new ServiceException(ServiceException.StatusCode.INVALID_REQUEST, "DELETE", "LocationIdInvalid", "location not found");
        }
        vendor.getLocations().remove(location);
        AddressDao.instance().delete(location.getId());
    }

    /**
     * Get Vendor Locations
     *
     * @param id
     * @param start
     * @param limit
     * @return
     *
     */
    @GET
    @Path("/vendorlocation/{id}/{start}/{limit}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CONTRACTS','ROLE_BILLING_AND_INVOICING','ROLE_CONTRACTS_FULL_VIEW')")
    public AddressTable getVendorLocations(@PathParam("id") long id, @PathParam("start") int start,
            @PathParam("limit") int limit) {
        AddressTable tableObj = new AddressTable();
        Vendor evendor = (Vendor) getDao().findById(id);
        tableObj.setEntities(evendor.getLocations());
        tableObj.setSize((long) evendor.getLocations().size());
        return tableObj;
    }

    @GET
    @Path("/locations/dropdown/{id}/{start}/{limit}")
    public List<Entry> getVendorLocationsDropDown(@PathParam("id") long id, @PathParam("start") int start, @PathParam("limit") int limit,
            @QueryParam("column") List<String> columns) {
        Vendor vendor = VendorDao.instance().findById(id);
        List<Entry> result = new ArrayList<>();
        for (Address address : vendor.getLocations()) {
            Entry entry = new Entry();
            entry.setId(address.getId().toString());
            entry.setValue(address.getStreet1() + "-" + address.getCity() + "-" + address.getState());
            result.add(entry);
        }
        return result;
    }

    @PUT
    @Path("/searchEmployee/{start}/{limit}")
    public String searchEmployee(Address address, @PathParam("start") int start, @PathParam("limit") int limit) {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT vendor from ").append(Vendor.class.getCanonicalName());
        queryStr.append("  where ");

        if (StringUtils.isNotBlank(address.getCity())) {
            queryStr.append("address.city LIKE '%").append(address.getCity().trim()).append("%' ").append(" and ");
        }
        if (StringUtils.isNotBlank(address.getState())) {
            queryStr.append("address.state LIKE '%").append(address.getState().trim()).append("%' ").append(" and ");
        }
        return queryStr.toString().substring(0, queryStr.toString().lastIndexOf("and"));
    }

    @XmlRootElement
    @XmlType
    public static class VendorTable implements java.io.Serializable {

        protected Long size;
        protected List<Vendor> entities;

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        @XmlElement
        public List<Vendor> getEntities() {
            return entities;
        }

        public void setEntities(List<Vendor> entities) {
            this.entities = entities;
        }
    }
}
