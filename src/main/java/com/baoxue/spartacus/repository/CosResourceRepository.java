package com.baoxue.spartacus.repository;

import com.baoxue.spartacus.pojo.CosResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


/**
 * 
 * @author: lvchao
 * @mail: chao9038@hnu.edu.cn
 * @time: 2018年1月18日上午9:01:04
 */
public interface CosResourceRepository extends JpaRepository<CosResource, Long>, JpaSpecificationExecutor<CosResource> {

    @Query(value="SELECT tags FROM tb_cos_resource WHERE cos_type=0 AND tags != ''", nativeQuery = true)
    List<String> findAllTags();

//    @Transactional
    @Modifying
    void deleteCosResourcesByIdIn(List<Long> ids);

//    @Transactional
    @Modifying
    @Query(value = "UPDATE tb_cos_resource SET acl_flag=?1 WHERE _key IN (?2)", nativeQuery = true)
    Integer batchSetAclFlag(int aclFlag, List<String> keys);

    CosResource findFirstByKey(String key);

    CosResource findFirstByKeyStartsWith(String key);

    Page<CosResource> findByKeyStartsWithAndCosType(String key, Integer cosType, Pageable pageable);

    List<CosResource> findAllByKeyIn(List<String> keys);

    List<CosResource> findAllByCosType(Integer cosType);

    List<CosResource> findAllByKeyStartsWithAndCosType(String key, Integer cosType);

    @Query(value="SELECT * FROM tb_cos_resource WHERE LOCATE(?1,_key) > 0 AND _key NOT REGEXP '/$' AND FIND_IN_SET(?4,tags) ORDER BY last_modified DESC LIMIT ?2,?3", nativeQuery = true)
    List<CosResource> getRecursiveCosResourcesByTag(String dirPath, Integer startIndex, Integer pageSize, String tag);

    @Query(value="SELECT COUNT(id) FROM tb_cos_resource WHERE LOCATE(?1,_key) > 0 AND _key NOT REGEXP '/$' AND FIND_IN_SET(?2,tags)", nativeQuery = true)
    Integer getRecursiveCosResourcesCountByTag(String dirPath, String tag);

    @Query(value="SELECT * FROM tb_cos_resource WHERE LOCATE(?1,_key) > 0 AND _key NOT REGEXP '/$' ORDER BY last_modified DESC LIMIT ?2,?3", nativeQuery = true)
    List<CosResource> getRecursiveCosResources(String dirPath, Integer startIndex, Integer pageSize);

    @Query(value="SELECT COUNT(id) FROM tb_cos_resource WHERE LOCATE(?1,_key) > 0 AND _key NOT REGEXP '/$'", nativeQuery = true)
    Integer getRecursiveCosResourcesCount(String dirPath);

    @Query(value="SELECT * FROM tb_cos_resource WHERE LOCATE(?1,_key) > 0 AND (LENGTH(?1)-LENGTH(REPLACE(?1,'/',''))) = (LENGTH(_key)-LENGTH(REPLACE(_key,'/',''))) AND _key NOT REGEXP '/$' AND FIND_IN_SET(?4,tags) ORDER BY last_modified DESC LIMIT ?2,?3", nativeQuery = true)
    List<CosResource> getDirectCosResourcesByTag(String dirPath, Integer startIndex, Integer pageSize, String tag);

    @Query(value="SELECT COUNT(id) FROM tb_cos_resource WHERE LOCATE(?1,_key) > 0 AND (LENGTH(?1)-LENGTH(REPLACE(?1,'/',''))) = (LENGTH(_key)-LENGTH(REPLACE(_key,'/',''))) AND _key NOT REGEXP '/$' AND FIND_IN_SET(?2,tags)", nativeQuery = true)
    Integer getDirectCosResourcesCountByTag(String dirPath, String tag);

    @Query(value="SELECT * FROM tb_cos_resource WHERE LOCATE(?1,_key) > 0 AND (LENGTH(?1)-LENGTH(REPLACE(?1,'/',''))) = (LENGTH(_key)-LENGTH(REPLACE(_key,'/',''))) AND _key NOT REGEXP '/$' ORDER BY last_modified DESC LIMIT ?2,?3", nativeQuery = true)
    List<CosResource> getDirectCosResources(String dirPath, Integer startIndex, Integer pageSize);

    @Query(value="SELECT COUNT(id) FROM tb_cos_resource WHERE LOCATE(?1,_key) > 0 AND (LENGTH(?1)-LENGTH(REPLACE(?1,'/',''))) = (LENGTH(_key)-LENGTH(REPLACE(_key,'/',''))) AND _key NOT REGEXP '/$'", nativeQuery = true)
    Integer getDirectCosResourcesCount(String dirPath);

}
