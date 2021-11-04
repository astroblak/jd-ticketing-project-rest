package com.javaapp.implementation;

import com.javaapp.dto.RoleDTO;
import com.javaapp.entity.Role;
import com.javaapp.exception.TicketingProjectException;
import com.javaapp.util.MapperUtil;
import com.javaapp.repository.RoleRepository;
import com.javaapp.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {


    private RoleRepository roleRepository;
    private MapperUtil mapperUtil;

    public RoleServiceImpl(RoleRepository roleRepository, MapperUtil mapperUtil) {
        this.roleRepository = roleRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    public List<RoleDTO> listAllRoles() {
        List<Role> list = roleRepository.findAll();
        return list.stream().map(obj -> {return mapperUtil.convert(obj, new RoleDTO());}).collect(Collectors.toList());
    }

    @Override
    public RoleDTO findById(Long id) throws TicketingProjectException {
        Role role = roleRepository.findById(id).orElseThrow(() -> new TicketingProjectException("Role does not exist"));
        return mapperUtil.convert(role, new RoleDTO());
    }
}
