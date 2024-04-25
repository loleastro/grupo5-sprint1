package org.mercadolibre.NotNullTeam.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.mercadolibre.NotNullTeam.DTO.response.BuyerResponseDTO;
import org.mercadolibre.NotNullTeam.DTO.response.BuyerResponseWithNotSellerListDTO;
import org.mercadolibre.NotNullTeam.DTO.response.SellerResponseWithNotBuyerListDTO;
import org.mercadolibre.NotNullTeam.exception.error.NotFoundException;
import org.mercadolibre.NotNullTeam.model.Buyer;
import org.mercadolibre.NotNullTeam.model.Seller;
import org.mercadolibre.NotNullTeam.repository.IBuyerRepository;
import org.mercadolibre.NotNullTeam.repository.ISellerRepository;
import org.mercadolibre.NotNullTeam.service.IBuyerService;
import org.mercadolibre.NotNullTeam.service.ISellerService;
import org.mercadolibre.NotNullTeam.service.ISellerServiceInternal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class BuyerServiceImpl implements IBuyerService {
    final IBuyerRepository iBuyerRepository;
    final ISellerRepository iSellerRepository;
    final ISellerServiceInternal iSellerService;


    final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void followSeller(Long userId, Long sellerToFollowId) {

        Buyer buyer = this.findBuyerById(userId);
        Seller seller =iSellerService.findById(sellerToFollowId);

        buyer.addNewFollowed(seller);
        seller.addNewFollower(buyer);
    }

    @Override
    public List<BuyerResponseWithNotSellerListDTO> getAll() {
        return iBuyerRepository
                .findAll()
                .stream()
                .map(e -> new BuyerResponseWithNotSellerListDTO(e.getUser().getId(), e.getUser().getName())).toList();
    }

    @Override
    public BuyerResponseDTO getFollowedList(Long userId) {
        Buyer buyer =
                this.findBuyerById(userId);

        return new BuyerResponseDTO(
                buyer.getUser().getId(),
                buyer.getUser().getName(),
                buyer.getFollowedList().stream().map(
                        s -> new SellerResponseWithNotBuyerListDTO(
                                s.getUser().getId(),
                                s.getUser().getName()
                        )
                ).toList()
        );
    }

    public void unfollowSeller(Long userId, Long userIdToUnfollow) {
        Buyer buyer = this.findBuyerById(userId);
        Seller seller = iSellerService.findById(userIdToUnfollow);

        buyer.removeFollowed(seller);
        seller.removeFollower(buyer);
    }

    public Buyer findBuyerById(Long id){
        return iBuyerRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Buyer"));
    }

}
