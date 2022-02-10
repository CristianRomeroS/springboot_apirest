package com.formacionspring.app.apirest.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.formacionspring.app.apirest.dao.ClienteDao;
import com.formacionspring.app.apirest.entity.Cliente;

@Service
public class ClienteServiceImpl implements ClienteService {
	@Autowired
	private ClienteDao clienteDao;
	@Override
	@Transactional(readOnly = true)
	public List<Cliente> findAll() {
		// TODO Auto-generated method stub
		return (List<Cliente>) clienteDao.findAll();
	}
	@Override
	@Transactional(readOnly = true)
	public Cliente findById(long id) {
		// TODO Auto-generated method stub
		return clienteDao.findById(id).orElse(null);
	}
	@Override
	@Transactional
	public Cliente save(Cliente cliente) {
		// TODO Auto-generated method stub
		return clienteDao.save(cliente);
	}
	@Override
	@Transactional
	public void delete(long id) {
		
		clienteDao.deleteById(id);
	}

}
