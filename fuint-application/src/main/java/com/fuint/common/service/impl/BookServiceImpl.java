package com.fuint.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fuint.common.dto.BookDto;
import com.fuint.common.dto.DayDto;
import com.fuint.common.dto.TimeDto;
import com.fuint.common.param.BookPage;
import com.fuint.common.param.BookableParam;
import com.fuint.common.service.BookService;
import com.fuint.common.service.StoreService;
import com.fuint.common.util.DateUtil;
import com.fuint.framework.annoation.OperationServiceLog;
import com.fuint.framework.exception.BusinessCheckException;
import com.fuint.framework.pagination.PaginationResponse;
import com.fuint.repository.mapper.MtBookItemMapper;
import com.fuint.repository.mapper.MtBookMapper;
import com.fuint.repository.model.MtBanner;
import com.fuint.common.service.SettingService;
import com.fuint.common.enums.StatusEnum;
import com.fuint.repository.model.MtBook;
import com.fuint.repository.model.MtStore;
import com.fuint.utils.StringUtil;
import com.github.pagehelper.PageHelper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.pagehelper.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 预约服务接口
 *
 * Created by FSQ
 * CopyRight https://www.fuint.cn
 */
@Service
@AllArgsConstructor(onConstructor_= {@Lazy})
public class BookServiceImpl extends ServiceImpl<MtBookMapper, MtBook> implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    private MtBookMapper mtBookMapper;

    private MtBookItemMapper mtBookItemMapper;

    /**
     * 系统设置服务接口
     * */
    private SettingService settingService;

    /**
     * 店铺接口
     */
    private StoreService storeService;

    /**
     * 分页查询预约列表
     *
     * @param bookPage
     * @return
     */
    @Override
    public PaginationResponse<BookDto> queryBookListByPagination(BookPage bookPage) {
        Page<MtBanner> pageHelper = PageHelper.startPage(bookPage.getPage(), bookPage.getPageSize());
        LambdaQueryWrapper<MtBook> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.ne(MtBook::getStatus, StatusEnum.DISABLE.getKey());

        String name = bookPage.getName();
        if (StringUtils.isNotBlank(name)) {
            lambdaQueryWrapper.like(MtBook::getName, name);
        }
        Integer cateId = bookPage.getCateId();
        if (cateId != null && cateId > 0) {
            lambdaQueryWrapper.like(MtBook::getCateId, cateId);
        }
        if (StringUtils.isNotBlank(bookPage.getStatus())) {
            lambdaQueryWrapper.eq(MtBook::getStatus, bookPage.getStatus());
        }
        Integer merchantId = bookPage.getMerchantId();
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(MtBook::getMerchantId, merchantId);
        }
        if (bookPage.getStoreId() != null) {
            lambdaQueryWrapper.and(wq -> wq
                    .eq(MtBook::getStoreId, 0)
                    .or()
                    .eq(MtBook::getStoreId, bookPage.getStoreId()));
        }

        lambdaQueryWrapper.orderByAsc(MtBook::getSort);
        List<MtBook> bookList = mtBookMapper.selectList(lambdaQueryWrapper);
        List<BookDto> dataList = new ArrayList<>();
        String baseImage = settingService.getUploadBasePath();
        if (bookList != null && bookList.size() > 0) {
            for (MtBook mtBook : bookList) {
                 BookDto bookDto = new BookDto();
                 BeanUtils.copyProperties(mtBook, bookDto);
                 bookDto.setLogo(baseImage + mtBook.getLogo());
                 dataList.add(bookDto);
            }
        }

        PageRequest pageRequest = PageRequest.of(bookPage.getPage(), bookPage.getPageSize());
        PageImpl pageImpl = new PageImpl(dataList, pageRequest, pageHelper.getTotal());
        PaginationResponse<BookDto> paginationResponse = new PaginationResponse(pageImpl, BookDto.class);
        paginationResponse.setTotalPages(pageHelper.getPages());
        paginationResponse.setTotalElements(pageHelper.getTotal());
        paginationResponse.setContent(dataList);

        return paginationResponse;
    }

    /**
     * 添加预约项目
     *
     * @param mtBook 预约信息
     * @return
     */
    @Override
    @OperationServiceLog(description = "添加预约项目")
    public MtBook addBook(MtBook mtBook) throws BusinessCheckException {
        Integer storeId = mtBook.getStoreId() == null ? 0 : mtBook.getStoreId();
        if (mtBook.getMerchantId() == null || mtBook.getMerchantId() <= 0) {
            MtStore mtStore = storeService.queryStoreById(storeId);
            if (mtStore != null) {
                mtBook.setMerchantId(mtStore.getMerchantId());
            }
        }
        if (mtBook.getMerchantId() == null || mtBook.getMerchantId() <= 0) {
            throw new BusinessCheckException("新增预约失败：所属商户不能为空！");
        }
        if (StringUtil.isEmpty(mtBook.getName())) {
            throw new BusinessCheckException("新增预约失败：项目名称不能为空！");
        }
        if (StringUtil.isEmpty(mtBook.getLogo())) {
            throw new BusinessCheckException("新增预约失败：封面图片不能为空！");
        }
        mtBook.setStoreId(storeId);
        mtBook.setStatus(StatusEnum.ENABLED.getKey());
        mtBook.setUpdateTime(new Date());
        mtBook.setCreateTime(new Date());
        Integer id = mtBookMapper.insert(mtBook);
        if (id > 0) {
            return mtBook;
        } else {
            logger.error("新增预约失败.");
            throw new BusinessCheckException("抱歉，新增预约失败！");
        }
    }

    /**
     * 根据ID获取预约项目信息
     *
     * @param id 预约项目ID
     * @param fillDate 填充日期
     * @return
     */
    @Override
    public BookDto getBookById(Integer id, boolean fillDate) throws ParseException {
        BookDto bookDto = new BookDto();
        MtBook mtBook = mtBookMapper.selectById(id);
        if (mtBook == null) {
            return null;
        }
        BeanUtils.copyProperties(mtBook, bookDto);

        List<DayDto> dateList = new ArrayList<>();
        String serviceDates = mtBook.getServiceDates();

        // 未填写日期，则未来7天都可以预约
        if (StringUtil.isEmpty(serviceDates)) {
            List<String> dates = new ArrayList<>();
            LocalDate today = LocalDate.now();
            for (int i = 0; i < 7; i++) {
                 LocalDate date = today.plusDays(i + 1);
                 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                 String dateString = date.format(formatter);
                 dates.add(dateString);
            }
            serviceDates = String.join(",", dates);
            if (fillDate) {
                bookDto.setServiceDates(serviceDates);
            }
        }

        if (StringUtil.isNotEmpty(serviceDates)) {
            List<String> dates = Arrays.asList(serviceDates.split(",").clone());
            if (dates.size() > 0) {
                for (String date : dates) {
                    Date currentDate = DateUtil.parseDate(date + " 23:59:59");
                    Date now = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("EEEE", Locale.CHINA);
                    String week = format.format(currentDate);
                    DayDto day = new DayDto();
                    day.setWeek(week);
                    day.setDate(DateUtil.formatDate(currentDate, "MM-dd"));
                    if (now.compareTo(currentDate) <= 0) {
                        day.setEnable(true);
                    } else {
                        day.setEnable(false);
                    }
                    dateList.add(day);
                }
            }
        }
        bookDto.setDateList(dateList);

        List<TimeDto> timeList = new ArrayList<>();
        String serviceTimes = mtBook.getServiceTimes();

        // 未填写时段，则未来
        if (StringUtil.isEmpty(serviceTimes)) {
            serviceTimes = "08:30-12:00-1,14:00-18:00-1";
        }

        if (StringUtil.isNotEmpty(serviceTimes) && bookDto.getDateList().size() > 0) {
            List<String> times = Arrays.asList(serviceTimes.split(",").clone());
            if (times.size() > 0) {
                for (String time : times) {
                     TimeDto timeDto = new TimeDto();
                     String arr[] = time.split("-");
                     timeDto.setTime(arr[0] + "-" + arr[1]);
                     timeDto.setEnable(true);
                     timeList.add(timeDto);
                }
            }
        }
        bookDto.setTimeList(timeList);

        return bookDto;
    }

    /**
     * 修改预约项目
     *
     * @param  mtBook
     * @throws BusinessCheckException
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @OperationServiceLog(description = "修改预约项目")
    public MtBook updateBook(MtBook mtBook) throws BusinessCheckException {
        MtBook book = mtBookMapper.selectById(mtBook.getId());
        if (book == null) {
            throw new BusinessCheckException("该预约项目状态异常");
        }
        if (mtBook.getLogo() != null) {
            book.setLogo(mtBook.getLogo());
        }
        if (mtBook.getCateId() != null) {
            book.setCateId(mtBook.getCateId());
        }
        if (book.getName() != null) {
            book.setName(mtBook.getName());
        }
        if (mtBook.getStoreId() != null) {
            book.setStoreId(mtBook.getStoreId());
        }
        if (mtBook.getDescription() != null) {
            book.setDescription(mtBook.getDescription());
        }
        if (mtBook.getOperator() != null) {
            book.setOperator(mtBook.getOperator());
        }
        if (mtBook.getStatus() != null) {
            book.setStatus(mtBook.getStatus());
        }
        if (mtBook.getGoodsId() != null) {
            book.setGoodsId(mtBook.getGoodsId());
        }
        if (mtBook.getSort() != null) {
            book.setSort(mtBook.getSort());
        }
        if (mtBook.getServiceDates() != null) {
            book.setServiceDates(mtBook.getServiceDates());
        }
        if (mtBook.getServiceTimes() != null) {
            book.setServiceTimes(mtBook.getServiceTimes());
        }
        if (mtBook.getServiceStaffIds() != null) {
            book.setServiceStaffIds(mtBook.getServiceStaffIds());
        }
        book.setUpdateTime(new Date());
        mtBookMapper.updateById(book);
        return book;
    }

    /**
     * 是否可预约
     *
     * @param  param
     * @throws BusinessCheckException
     * @return
     * */
    @Override
    public List<String> isBookable(BookableParam param) throws BusinessCheckException,ParseException {
       MtBook mtBook = mtBookMapper.selectById(param.getBookId());
       List<String> result = new ArrayList<>();
       if (mtBook == null) {
           throw new BusinessCheckException("预约项目不存在");
       }

       List<String> bookList = new ArrayList<>();
       if (StringUtil.isNotEmpty(param.getDate())) {
           bookList = mtBookItemMapper.getBookList(param.getBookId(), param.getDate(), param.getTime());
       }
       Integer bookNum = bookList.size();

       Integer limit = 0;
       String serviceTime = mtBook.getServiceTimes();

       // 未填写时段，则未来
       if (StringUtil.isEmpty(serviceTime)) {
           serviceTime = "08:30-12:00-1,14:00-18:00-1";
       }

       if (StringUtil.isNotEmpty(serviceTime)) {
           String[] times = serviceTime.split(",");
           if (times.length > 0) {
               for (String str : times) {
                    if (str.indexOf(param.getTime()) >= 0) {
                        String[] timeArr = str.split("-");
                        if (timeArr.length > 2) {
                            limit = Integer.parseInt(timeArr[2]);
                        }
                    }
               }
           }
       }
       Date now = new Date();
       if (bookNum < limit) {
           if (StringUtil.isNotEmpty(param.getTime())) {
               String[] arr = param.getTime().split("-");
               String dateTime = param.getDate() + " " + arr[1]+":00";
               Date currentDate = DateUtil.parseDate(dateTime);
               if (now.compareTo(currentDate) < 0) {
                   result.add(param.getTime());
               }
           } else {
               String[] times = mtBook.getServiceTimes().split(",");
               if (times.length > 0) {
                   for (String str : times) {
                        String[] arr = str.split("-");
                        if (arr.length > 2) {
                            String item = arr[0] + "-" + arr[1];
                            String dateTime = param.getDate() + " " + arr[1]+":00";
                            Date currentDate = DateUtil.parseDate(dateTime);
                            if (!bookList.contains(item) && now.compareTo(currentDate) < 0) {
                                result.add(item);
                            }
                        }
                   }
               }
           }
       }
       return result;
    }

    /**
     * 获取预约项目列表
     *
     * @param  merchantId 商户ID
     * @param  storeId 店铺ID
     * @return
     * */
    public List<MtBook> getBookList(Integer merchantId, Integer storeId) {
        LambdaQueryWrapper<MtBook> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(MtBook::getStatus, StatusEnum.ENABLED.getKey());
        if (merchantId != null && merchantId > 0) {
            lambdaQueryWrapper.eq(MtBook::getMerchantId, merchantId);
        }
        if (storeId != null && storeId > 0) {
            lambdaQueryWrapper.eq(MtBook::getStoreId, storeId);
        }

        lambdaQueryWrapper.orderByAsc(MtBook::getSort);
        List<MtBook> dataList = mtBookMapper.selectList(lambdaQueryWrapper);
        String baseImage = settingService.getUploadBasePath();

        if (dataList.size() > 0) {
            for (MtBook book : dataList) {
                book.setLogo(baseImage + book.getLogo());
            }
        }

        return dataList;
    }
}
