/**
 * System Soft Technolgies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.jrs.client;

import info.chili.service.jrs.exception.ServiceException;
import info.chili.service.jrs.types.Entry;
import info.chili.dao.CRUDDao;
import info.yalamanchili.office.dao.client.ClientDao;
import info.yalamanchili.office.dao.client.VendorDao;
import info.yalamanchili.office.dao.profile.AddressDao;
import info.yalamanchili.office.dao.profile.ContactDao;
import info.yalamanchili.office.dto.profile.ContactDto;
import info.yalamanchili.office.dto.profile.ContactDto.ContactDtoTable;
import info.yalamanchili.office.entity.client.Client;
import info.yalamanchili.office.entity.client.Vendor;
import info.yalamanchili.office.entity.profile.Address;
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
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

/**
 *
 * @author Prashanthi
 */
@Path("secured/vendor")
@Component
@Scope("request")
public class VendorResource extends CRUDResource<Vendor> {

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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_HR','ROLE_TIME','ROLE_EXPENSE','ROLE_RELATIONSHIP')")
    public VendorTable table(@PathParam("start") int start, @PathParam("limit") int limit) {
        VendorTable tableObj = new VendorTable();
        tableObj.setEntities(getDao().query(start, limit));
        tableObj.setSize(getDao().size());
        return tableObj;
    }

    @PUT
    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TIME','ROLE_EXPENSE')")
    public Vendor save(Vendor vendor) {
        return super.save(vendor);
    }

    @PUT
    @Path("/delete/{id}")
    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TIME','ROLE_EXPENSE')")
    public void delete(Long id) {
        super.delete(id);
    }

    /**
     * Add Vendor Contact
     *
     * @param vendorId
     * @param dto
     *
     */
    @PUT
    @Path("/vendorcontact/{vendorId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TIME','ROLE_EXPENSE')")
    public void addvendorContact(@PathParam("vendorId") Long vendorId, ContactDto dto) {
        Vendor vendor = (Vendor) getDao().findById(vendorId);
        Contact contact = contactService.save(dto);
        vendor.addContact(contact);
    }

    @PUT
    @Path("/contact/remove/{vendorId}/{contactId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TIME','ROLE_EXPENSE')")
    public void removeContact(@PathParam("vendorId") Long vendorId, @PathParam("contactId") Long contactId) {
        Vendor vendor = (Vendor) getDao().findById(vendorId);
        if (vendor == null) {
            throw new ServiceException(ServiceException.StatusCode.INVALID_REQUEST, "DELETE", "vendorIdInvalid", "vendor not found");
        }
        Contact contact = ContactDao.instance().findById(contactId);
        if (contact == null) {
            throw new ServiceException(ServiceException.StatusCode.INVALID_REQUEST, "DELETE", "contactIdInvalid", "contact not found");
        }
        vendor.getContacts().remove(contact);
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_HR','ROLE_TIME','ROLE_EXPENSE','ROLE_RELATIONSHIP')")
    public ContactDtoTable getVendorContacts(@PathParam("id") long id, @PathParam("start") int start,
            @PathParam("limit") int limit) {
        ContactDtoTable tableObj = new ContactDtoTable();
        Vendor vendors = (Vendor) getDao().findById(id);
        List<ContactDto> dtos = new ArrayList<ContactDto>();
        for (Contact entity : vendors.getContacts()) {
            dtos.add(ContactMapper.map(entity));
        }
        tableObj.setEntities(dtos);
        tableObj.setSize((long) vendors.getContacts().size());
        return tableObj;
    }

    @GET
    @Path("/contacts/dropdown/{id}/{start}/{limit}")
    public List<Entry> getVendorContactsDropDown(@PathParam("id") long id, @PathParam("start") int start, @PathParam("limit") int limit,
            @QueryParam("column") List<String> columns) {
        Vendor vendor = VendorDao.instance().findById(id);
        List<Entry> result = new ArrayList<Entry>();
        for (Contact contact : vendor.getContacts()) {
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
    @Path("/vendorlocation/{vendorId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TIME','ROLE_EXPENSE')")
    public void addvendorlocation(@PathParam("vendorId") Long vendorId, Address address) {
        Vendor vend = (Vendor) getDao().findById(vendorId);
        vend.addLocations(address);
    }

    @PUT
    @Path("/location/remove/{vendorId}/{locationId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_TIME','ROLE_EXPENSE')")
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_HR','ROLE_TIME','ROLE_EXPENSE','ROLE_RELATIONSHIP')")
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
        List<Entry> result = new ArrayList<Entry>();
        for (Address address : vendor.getLocations()) {
            Entry entry = new Entry();
            entry.setId(address.getId().toString());
            entry.setValue(address.getStreet1() + " " + address.getCity());
            result.add(entry);
        }
        return result;
    }

    @XmlRootElement
    @XmlType
    public static class VendorTable {

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
