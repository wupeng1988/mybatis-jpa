Mybatis-Jpa：便捷版的Mybatis! 一切为了开发效率!
==========

> Mybatis由于非常灵活，现在几乎是互联网企业标配的持久层框架， 不过正是由于Mybatis过于灵活， 用起来反而有些繁琐。
每一个Mapper都需要一个xml文件， 都需要配置`ResultMap`, 甚至于一些公共方法也得通过代码生成的方式生成到xml文件
里面。基于这种情况，为了快速提高开发效率，才诞生了Mybatis-Jpa。
当然，有人力物力的大型企业基本上都有自己对Mybatis的封装，那么对于没有人力的小型企业或者个人开发者，
不妨看下Mybatis-Jpa这个便捷版的Mybatis是如何提高开发效率的。**`Mybatis-Jpa`目前仅支持MySql**。

Quick Start
-------

1.  引入jar包：

		<dependency>
		    <groupId>org.singledog.spring.boot</groupId>
		    <artifactId>mybatis-spring-boot-starter</artifactId>
		    <version>1.3.4</version>
		</dependency>

2.  编写实体类Entity：

		@Entity
		@Table(name = "city")
		public class City {

			@Id
			private Integer id;
			@Column
			private String name;
			@Column
			private Integer postcode;
			@Column
			private String province;

			//getters and setters
		}


	类似JPA，实体类的属性上面，我们需要用`@Column`,`@Entity`,`@Table`来标注, **注意这几个注解要引用`org.apache.ibatis.features.jpa.annotation`包下面的**, 这点不太好，后续会想办法兼容JPA的注解。


3. 编写Mapper继承JpaMapper：


		@Mapper
		public interface CityMapper extends JpaMapper<City, Integer> {

		}

   除了继承`JpaMapper`之外，与正常Mybatis并无区别。继承之后`CityMapper`就有了`JpaMapper`所拥有的公共方法， `CityMapper`自己无须为这些方法写xml

   **PS. 如果不继承`JpaMapper`, 那么这些特性都不会有，就是普通的Mybatis的用法**.


到这里Mybatis-Jpa就集成好了，我们看一下都有哪些提高开发效率的特性。

Mybatis-Jpa特性
--------

1. 公共方法

	JpaMapper所拥有的方法都可以直接使用而无须配置xml，共有如下几个：

			//根据ID列表查找
			List<T> findAllById(List<ID> ids);
			//排序查询所有数据
			List<T> findAll(Sort sort);
			//分页查询所有数据
			Page<T> findAllByPage(Pageable pageable);
			//保存
			<S extends T> int save(S entity);
			//自增主键保存
			<S extends T> int saveAutoIncrementKey(S entity);
			//批量保存
			<S extends T> int saveAll(List<S> entities);
			//批量保存-自增主键
			<S extends T> int saveAllAutoIncrementKey(List<S> entities);
			//根据id查询
			T findById(ID id);
			//数量
			long count();

			int deleteById(ID id);

			int deleteAll(List<? extends ID> ids);
			//删除所有
			int clear();
			//根据主键选择性更新
			<S extends T> int updateByPrimaryKeySelective(S entity);
			//根据主键更新
			<S extends T> int updateByPrimaryKey(S entity);

			<S extends T> int saveSelective(S entity);

			<S extends T> int saveSelectiveAutoIncrement(S entity);
			//根据实体类对象查询一个结果
			T findOne(T example);
			// 根据实体类对象查询结果列表
			List<T> findList(T example);
			// 根据条件查询
			List<T> findByExample(Example<T> example);


2. 根据方法名查询

	用过spring-data-jpa的同学应该对他的方法名查询的特性印象深刻, 确实非常好用，所以在这里我也把这个按照方法名查询的特性加入了进来。

	方法名查询的特性目前仅支持查询和删除， 不支持更新，并且仅支持单表操作。方法名查询仅仅需要定义方法，同样无须编写xml， 方法名约定如下：

	1. 第一步还是要确保继承了`JpaMapper`。

	2. 查询的前缀支持 `select...by...` `find...by...` `get/read/query/load...by...`, 并且支持`And` `Or`。例如：

			//sql: select * from city where name = ? and postcode = ? order by name
			List<City> findByNameAndPostcodeOrderByName(String name, Integer postcode);

			//sql: select * from city where name = ? order by postcode desc
			List<City> findByNameOrderByPostcodeDesc(String name);

			//sql: select * from city where name = ? order by postcode desc limit 10
			List<City> findTop10ByNameOrderByPostcodeDesc(String name);
			List<City> findFirst10ByNameOrderByPostcodeDesc(String name);

			//sql: select * from city where name = ? order by postcode desc limit 1
			City findOneByNameOrderByPostcodeDesc(String name);
			City findTopByNameOrderByPostcode(String name);
			City findFirstByNameOrderByPostcodeDesc(String name);

			// distinct support
			//sql: select distinct * from city where name = ? order by postcode
			List<City> findDistinctByNameOrderByPostcode(String name);

			//sql: select distinct * from city where name = ? order by postcode limit 10
			List<City> findDistinctTop10ByNameOrderByPostcode(String name);

			//sql: select * from city where name=? or postcode=? order by posotcode desc
			List<City> findByNameOrPostcodeOrderByPostcodeDesc(String name, int postcode);

		比较运算符： 比较运算符目前支持`Equals` `Equals` `Is` `Between` `Lessthan` `LessThanEqual` `LessThanEquals` `GreaterThan` `GreaterThanEqual` `GreaterThanEquals` `After` `Before` `IsNull` `IsNotNull` `NotNull` `Like` `NotLike` `In` `NotIn`

			//sql: select * from city where name like ? and postcode in (?,?,?...) order by postcode
			List<City> findByNameLikeAndPostcodeInOrderByPostcode(String name, List<Integer> postcodes);

			//sql: select * from city where name like ? and postcode between ? and ? order by postcode desc
			List<City> findByNameLikeAndPostcodeBetweenOrderByPostcodeDesc(String name, int start, int end);

			//sql: select * from city where name = ? or postcode >= ? order by postcode desc
			List<City> findByNameOrPostcodeGreaterThanEqualOrderByPostcodeDesc(String name, int postcode);

			//sql: select * from city where name is null and postcde is not null
			List<City> findByNameIsNullAndPostcodeIsNotNull();

			//分页查询支持: select * from city where name like ? limit ...
			Page<City> findByNameLike(String name, Pageable pageable);

			//分页支持: select * from city where name like ? and postcode < ? limit ...
			Page<City> findByNameLikeAndPostcodeLessThan(String name, int maxPostcode, Pageable pageable);

			//排序支持： select * from city where name like and ppostcode < ? order by ...
			List<City> findByNameLikeAndPostcodeLessThanEqual(String name, int maxPostcode, Sort sort);

		 **需要注意， Sort与Pageable参数不能与findTop5这种方法名定义一起使用， 如果使用findTop5这种形式请使用find...OrderBy...**

	3. 删除的前缀支持`delete/remove/del...by...`, 条件的约定于上述一致。

3. 根据实体查询

			City city = new City();
			city.setName("BeiJing");

			List<City> cities = cityMapper.findList(city);


4. 根据Example条件查询：

			Example<City> example1 = new Example<City>(City.class);
			example1.createCriteria().andEqualTo("postcode", 1001);//这里是属性  不是列明

			List<City> cities = cityMapper.findByExample(example1);


5. 分页/排序支持:

	我们在`CityMapper`中定义如下方法:

			Page<City> findCityByPage(@Param("name") String name, @Param("page") Pageable pageable);

			List<City> findCityListByPage(@Param("name") String name, @Param("page") Pageable pageable);

			List<City> findCityBySort(@Param("name") String name, @Param("sort") Sort sort);

	然后正常在xml中定义sql:

			<!-- 内置了一个id为default的ResultMap， 如果查询没有定义ResultMap，则会使用内置的ResultMap -->
			<select id="findCityByPage" parameterType="map" >
				<!-- all_columns 变量同样为内置 -->
				select <include refid="all_columns"/> from city where name like #{name}
			</select>

			<select id="findCityListByPage" parameterType="map" >

				select <include refid="all_columns"/> from city where name like #{name}
			</select>

			<select id="findCityBySort" parameterType="map" >
				select <include refid="all_columns"/> from city where name like #{name}
			</select>

	可以看到，在xml中我们并没有处理Pageable和Sort参数，仅仅是正常定义了sql。

			Page<City> page = cityMapper.findCityByPage("BeiJing%", PageRequest.of(0, 10, Sort.Direction.DESC, "postcode"));

			List<City> page = cityMapper.findCityListByPage("BeiJing%", PageRequest.of(0, 10, Sort.Direction.DESC, "postcode"));

			List<City> page = cityMapper.findCityBySort("BeiJing%", new Sort(Sort.Direction.DESC, "postcode"));

6. 内置的变量

	上面已经提到过， 会有内置变量`all_columns` 标识所有列， `default` 默认的`ResultMap`, 从此更新表结构再也不用更新xml了。
	PS. 指定mapper.xml的位置，可以通过在application.properties里面配置

			mybatis.mapperLocations=classpath*:mybatis/**/*.xml

	就是让Mybatis去mybatis文件夹以及子文件夹下去找.xml的文件去加载。

后续会写一下其中功能点的简单实现. [GitHub地址](https://github.com/wupeng1988/mybatis-jpa)
