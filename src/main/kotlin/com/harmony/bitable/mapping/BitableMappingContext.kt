package com.harmony.bitable.mapping

import org.springframework.data.mapping.context.MappingContext

interface BitableMappingContext : MappingContext<BitablePersistentEntity<*>, BitablePersistentProperty>
