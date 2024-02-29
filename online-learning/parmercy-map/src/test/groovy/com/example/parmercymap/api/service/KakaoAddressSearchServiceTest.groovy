package com.example.parmercymap.api.service

import com.example.parmercymap.AbstractIntegrationContainerBaseTest
import org.springframework.beans.factory.annotation.Autowired

class KakaoAddressSearchServiceTest extends AbstractIntegrationContainerBaseTest {

    @Autowired
    private KakaoAddressSearchService kakaoAddressSearchService

    def "address 파라미터 값이 null이면, requestAddressSearch null을 리턴한다."() {
        given:
        String address = null

        when:
        def result = kakaoAddressSearchService.requestAddressSearch(address);

        then:
        result == null
    }

    def "address가 valid하다면, requestAddressSearch 정상적으로 document를 리턴한다."() {
        given:
        String address = "서울 성북구"

        when:
        def result = kakaoAddressSearchService.requestAddressSearch(address);

        then:
        result.documentList.size() > 0
        result.metaDto.getTotalCount() > 0
        result.documentList.get(0).getAddressName() != null
    }
}
