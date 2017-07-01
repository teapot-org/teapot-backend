package org.teapot.backend.model.meta;

import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.*;

@Entity
@Table(name = "action")
public class TeapotAction extends AbstractPersistable<Long> {

    @Column(nullable = false, unique = true, length = 32)
    private String name;

    private String usage;

    @Lob
    @Column(length = 512)
    private String manual;

    public TeapotAction() {
    }

    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public String getManual() {
        return manual;
    }

    public void setManual(String manual) {
        this.manual = manual;
    }
}
