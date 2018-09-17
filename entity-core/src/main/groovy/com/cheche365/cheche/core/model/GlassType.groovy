package com.cheche365.cheche.core.model

import com.cheche365.cheche.core.repository.GlassTypeRepository
import com.cheche365.cheche.core.util.RuntimeUtil

import javax.persistence.Entity

@Entity
class GlassType extends AutoLoadEnum {

     static class Enum{
        public static GlassType DOMESTIC_1, IMPORT_2
        public static List<GlassType> ALL

        static {
            ALL = RuntimeUtil.loadEnum(GlassTypeRepository.class, GlassType.class, Enum.class)
        }
         static GlassType findByName(String name) {
            ALL.find {it.name == name}
        }

         static GlassType findById(Long id) {
            ALL.find {it.id == id}
        }
    }

}
