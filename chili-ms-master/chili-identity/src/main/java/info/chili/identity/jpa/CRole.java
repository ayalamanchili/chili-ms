/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package info.chili.identity.jpa;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

@Entity
@XmlRootElement
public class CRole implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long roleId;
    private String rolename;
    private Set<CRole> groups;

    public CRole(String rolename) {
        this.rolename = rolename;
    }

    @Id
    @GeneratedValue
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Index(name = "ROLE_NM")
    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    @ManyToMany(targetEntity = CRole.class)
    @JoinTable(name = "RoleGroups", joinColumns
            = @JoinColumn(name = "RoleId"), inverseJoinColumns
            = @JoinColumn(name = "GroupId"))
    @XmlTransient
    @ForeignKey(name = "FK_Groupes_CRoles")
    public Set<CRole> getGroups() {
        if (groups == null) {
            groups = new HashSet<CRole>();
        }
        return groups;
    }

    public void setGroups(Set<CRole> groups) {
        this.groups = groups;
    }
}
