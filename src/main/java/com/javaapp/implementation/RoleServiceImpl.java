package com.javaapp.implementation;

import com.javaapp.dto.RoleDTO;
import com.javaapp.entity.Role;
import com.javaapp.mapper.RoleMapper;
import com.javaapp.repository.RoleRepository;
import com.javaapp.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {


    private RoleRepository roleRepository;
    private RoleMapper roleMapper;

    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper = roleMapper;
    }

    @Override
    public List<RoleDTO> listAllRoles() {
        List<Role> list = roleRepository.findAll();
        return list.stream().map(obj -> {return roleMapper.convertToDto(obj);}).collect(Collectors.toList());
    }

    @Override
    public RoleDTO findById(Long id) {
        Role role = roleRepository.findById(id).get();
        return roleMapper.convertToDto(role);
    }
}
