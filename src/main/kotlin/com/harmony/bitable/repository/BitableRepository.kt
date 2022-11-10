package com.harmony.bitable.repository

import org.springframework.data.repository.CrudRepository

interface BitableRepository<T, ID> : CrudRepository<T, ID>
