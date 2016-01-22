/**
 * System Soft Technologies Copyright (C) 2013 ayalamanchili@sstech.mobi
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.jrs.client;

import com.google.common.base.Strings;
import info.chili.service.jrs.exception.ServiceException;
import info.chili.service.jrs.types.Entry;
import info.chili.dao.CRUDDao;
import info.chili.jpa.validation.Validate;
import info.chili.reporting.ReportGenerator;
import info.yalamanchili.office.cache.OfficeCacheKeys;
import info.yalamanchili.office.config.OfficeServiceConfiguration;
import info.yalamanchili.office.dao.client.VendorDao;
import info.yalamanchili.office.dao.profile.AddressDao;
import info.yalamanchili.office.dao.profile.ContactDao;
import info.yalamanchili.office.dao.profile.EmployeeDao;
import info.yalamanchili.office.dao.security.OfficeSecurityService;
import info.yalamanchili.office.dto.profile.ContactDto;
import info.yalamanchili.office.dto.profile.ContactDto.ContactDtoTable;
import info.yalamanchili.office.entity.client.Vendor;
import info.yalamanchili.office.entity.profile.Address;
import info.yalamanchili.office.entity.profile.Contact;
import info.yalamanchili.office.entity.profile.Employee;
import info.yalamanchili.office.entity.profile.Phone;
import info.yalamanchili.office.jms.MessagingService;
import info.yalamanchili.office.jrs.CRUDResource;
import info.yalamanchili.office.jrs.profile.AddressResource.AddressTable;
import info.yalamanchili.office.mapper.profile.ContactMapper;
import info.yalamanchili.office.profile.ContactService;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
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
    @Path("/updatecpd")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_CONTRACTS_ADMIN','ROLE_BILLING_AND_INVOICING')")
    @Validate
    @CacheEvict(value = OfficeCacheKeys.VENDOR, allEntries = true)
    public Vendor update(Vendor vendor, @QueryParam("submitForUpdateF") Boolean submitForUpdateF, @QueryParam("submitForUpdateP") Boolean submitForUpdateP) {
        vendor = super.save(vendor);
        if (submitForUpdateP || submitForUpdateF) {
            vendorDao.updateExistingClientInformations(vendor, submitForUpdateF, submitForUpdateP, OfficeSecurityService.instance().getCurrentUserName());
        }
        return vendor;
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

    @GET
    @Path("/vendorinfo-report")
    public void vendorReport() {
        List<VendorMasterReportDto> res = new ArrayList();
        for (Vendor vn : VendorDao.instance().query(0, 2000)) {
            VendorMasterReportDto dto = new VendorMasterReportDto();
            dto.setName(vn.getName());
            if (vn.getWebsite() != null) {
                dto.setWebSite(vn.getWebsite());
            }

            if (vn.getPaymentTerms() != null) {
                dto.setPaymentTerms(vn.getPaymentTerms());
            }

            if (vn.getVendorinvFrequency() != null) {
                dto.setInvFrequency(vn.getVendorinvFrequency().name().toLowerCase());
            }

            if (vn.getVendorType() != null) {
                dto.setVendorType(vn.getVendorType().name().toLowerCase());
            }

            if (vn.getVendorFees() != null) {
                dto.setVendorFees(vn.getVendorFees().toString());
            }
            // for getting vendor locations
            if (vn.getLocations().size() > 0) {
                String vendorAddressInfo = "\n";
                for (Address address : vn.getLocations()) {
                    vendorAddressInfo = vendorAddressInfo.concat(address.getStreet1() + "-");
                    if (address.getStreet2() != null) {
                        vendorAddressInfo = vendorAddressInfo.concat(address.getStreet2() + "-");
                    }
                    vendorAddressInfo = vendorAddressInfo.concat(address.getCity() + "-" + address.getState() + "-" + address.getCountry() + "-");
                    if (address.getZip() != null) {
                        vendorAddressInfo = vendorAddressInfo.concat(address.getZip() + "," + "\n");
                    }
                    dto.setVendorLocations(vendorAddressInfo);
                }
            }
            // for getting Recruiter Contacts
            if (vn.getContacts().size() > 0) {
                String recContact = "\n";
                for (Contact contact : vn.getContacts()) {
                    String name = "\n";
                    name = name.concat(contact.getFirstName() + " " + contact.getLastName());
                    recContact = recContact.concat(name + " " + "-");
                    if (contact.getEmails().size() > 0) {
                        String email = " ";
                        email = email.concat(contact.getEmails().get(0).getEmail());
                        recContact = recContact.concat(email + " " + "-");
                    }
                    if (contact.getPhones().size() > 0) {
                        for (Phone rphone : contact.getPhones()) {
                            if (rphone.getCountryCode() != null) {
                                String ccode = " ";
                                ccode = ccode.concat(rphone.getCountryCode());
                                recContact = recContact.concat(ccode + " " + "-");
                            }
                            String phone = " ";
                            phone = phone.concat(rphone.getPhoneNumber());
                            recContact = recContact.concat(phone + " " + "-");
                            if (rphone.getExtension() != null) {
                                String ext = " ";
                                ext = ext.concat(rphone.getExtension());
                                recContact = recContact.concat(ext + " " + ",");
                            }
                        }
                    }
                    dto.setRecruiterContact(recContact);
                }
            }
            // for getting AcctPay Contacts
            if (vn.getAcctPayContacts().size() > 0) {
                String actContact = "\n";
                for (Contact acpaycnt : vn.getAcctPayContacts()) {
                    String acname = "\n";
                    acname = acname.concat(acpaycnt.getFirstName() + " " + acpaycnt.getLastName());
                    actContact = actContact.concat(acname + " " + "-");
                    if (acpaycnt.getEmails().size() > 0) {
                        String acemail = " ";
                        acemail = acemail.concat(acpaycnt.getEmails().get(0).getEmail());
                        actContact = actContact.concat(acemail + " " + "-");
                    }
                    if (acpaycnt.getPhones().size() > 0) {
                        for (Phone acpayphone : acpaycnt.getPhones()) {
                            if (acpayphone.getCountryCode() != null) {
                                String acccode = " ";
                                acccode = acccode.concat(acpayphone.getCountryCode());
                                actContact = actContact.concat(acccode + " " + "-");
                            }
                            String acphone = " ";
                            acphone = acphone.concat(acpayphone.getPhoneNumber());
                            actContact = actContact.concat(acphone + " " + "-");
                            if (acpayphone.getExtension() != null) {
                                String acext = " ";
                                acext = acext.concat(acpayphone.getExtension());
                                actContact = actContact.concat(acext + " " + ",");
                            }
                        }
                    }
                }
                dto.setAcctPayContact(actContact);
            }
            res.add(dto);
        }
        String[] columnOrder = new String[]{"name", "webSite", "paymentTerms", "invFrequency", "vendorType", "vendorFees", "vendorLocations", "recruiterContact", "acctPayContact"};
        MessagingService.instance().emailReport(ReportGenerator.generateExcelOrderedReport(res, "Vendor-Info-Report", OfficeServiceConfiguration.instance().getContentManagementLocationRoot(), columnOrder), OfficeSecurityService.instance().getCurrentUser().getPrimaryEmail().getEmail());
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

    @GET
    @Path("/search/{searchText}/{start}/{limit}")
    @Transactional(propagation = Propagation.NEVER)
    @Override
    public List<Vendor> search(@PathParam("searchText") String searchText, @PathParam("start") int start,
            @PathParam("limit") int limit, @QueryParam("column") List<String> columns) {
        return getDao().sqlSearch(searchText, start, limit, columns, false);
    }

    @PUT
    @Path("/search-vendor/{start}/{limit}")
    public List<Vendor> search(VendorSearchDto dto, @PathParam("start") int start, @PathParam("limit") int limit) {
        TypedQuery<Vendor> q = em.createQuery(getSearchQuery(dto), Vendor.class);
        return q.getResultList();
    }

    protected String getSearchQuery(VendorSearchDto dto) {
        StringBuilder queryStr = new StringBuilder();
        queryStr.append("SELECT v from ").append(Vendor.class.getCanonicalName()).append(" as v");
        if (!Strings.isNullOrEmpty(dto.getCity()) || !Strings.isNullOrEmpty(dto.getState())) {
            queryStr.append(" join v.locations as vendorLocations");
        }
        queryStr.append("  where ");
        if (!Strings.isNullOrEmpty(dto.getVendorName())) {
            queryStr.append("name LIKE '%").append(dto.getVendorName().trim()).append("%' ").append(" and ");
        }
        if (dto.getVendorType() != null) {
            queryStr.append("vendorType LIKE '%").append(dto.getVendorType().name().trim()).append("%' ").append(" and ");
        }
        if (!Strings.isNullOrEmpty(dto.getCity())) {
            queryStr.append("lower(vendorLocations.city) = '").append(dto.getCity().toLowerCase().trim()).append("' ").append(" and ");
        }

        if (!Strings.isNullOrEmpty(dto.getState())) {
            queryStr.append("vendorLocations.state = '").append(dto.getState().trim()).append("' ").append(" and ");
        }
        return queryStr.toString().substring(0, queryStr.toString().lastIndexOf("and"));
    }

    @PUT
    @Path("/report")
    public void report(VendorSearchDto dto) {
        ClientResource.ClientDtoTable table = new ClientResource.ClientDtoTable();
        List<ClientDto> dtos = new ArrayList();
        ClientDto dto1 = null;
        TypedQuery<Vendor> q = em.createQuery(getSearchQuery(dto), Vendor.class);
        for (Vendor vendor : q.getResultList()) {
            dto1 = ClientDto.mapVendor(mapper, vendor, dto);
            dtos.add(dto1);
        }
        table.setEntities(dtos);
        table.setSize(Long.valueOf(dtos.size()));
        String[] columnOrder = new String[]{"name", "street1", "street2", "city", "state"};
        Employee emp = OfficeSecurityService.instance().getCurrentUser();
        if (dto.getCity() != null) {
            String fileName = ReportGenerator.generateExcelOrderedReport(table.getEntities(), "Vendors In City " + dto.getCity(), OfficeServiceConfiguration.instance().getContentManagementLocationRoot(), columnOrder);
            MessagingService.instance().emailReport(fileName, emp.getPrimaryEmail().getEmail());
        } else if (dto.getState() != null) {
            String fileName = ReportGenerator.generateExcelOrderedReport(table.getEntities(), "Vendors In State " + dto.getState(), OfficeServiceConfiguration.instance().getContentManagementLocationRoot(), columnOrder);
            MessagingService.instance().emailReport(fileName, emp.getPrimaryEmail().getEmail());
        }
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

    @PUT
    @Path("/search-vendor1/{start}/{limit}")
    public List<ClientDto> search1(VendorSearchDto dto, @PathParam("start") int start, @PathParam("limit") int limit) {
        List<ClientDto> dtos = new ArrayList();
        ClientDto dto1 = null;
        TypedQuery<Vendor> q = em.createQuery(getSearchQuery(dto), Vendor.class);
        for (Vendor client : q.getResultList()) {
            dto1 = ClientDto.mapVendor(mapper, client, dto);
            dtos.add(dto1);
        }
        return dtos;
    }

    @XmlRootElement
    @XmlType
    public static class ClientDtoTable implements java.io.Serializable {

        protected Long size;
        protected List<ClientDto> entities;

        public Long getSize() {
            return size;
        }

        public void setSize(Long size) {
            this.size = size;
        }

        @XmlElement
        public List<ClientDto> getEntities() {
            return entities;
        }

        public void setEntities(List<ClientDto> entities) {
            this.entities = entities;
        }
    }
}
