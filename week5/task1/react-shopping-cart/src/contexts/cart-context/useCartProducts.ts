import { useCartContext } from './CartContextProvider';
import useCartTotal from './useCartTotal';
import { ICartProduct } from 'models';
import { useCallback } from 'react';

const useCartProducts = () => {
  const { products, setProducts } = useCartContext();
  const { updateCartTotal } = useCartTotal();

  const updateQuantitySafely = useCallback((
    currentProduct: ICartProduct,
    targetProduct: ICartProduct,
    quantity: number
  ): ICartProduct => {
    if (currentProduct.id === targetProduct.id) {
      return {
        ...currentProduct,
        quantity: currentProduct.quantity + quantity,
      };
    } else {
      return currentProduct;
    }
  }, []);

  const addProduct = useCallback((newProduct: ICartProduct) => {
    let updatedProducts;
    const isProductAlreadyInCart = products.some(
      (product: ICartProduct) => newProduct.id === product.id
    );

    if (isProductAlreadyInCart) {
      updatedProducts = products.map((product: ICartProduct) =>
        updateQuantitySafely(product, newProduct, newProduct.quantity)
      );
    } else {
      updatedProducts = [...products, { ...newProduct }];
    }

    setProducts(updatedProducts);
    updateCartTotal(updatedProducts);
  }, [products, setProducts, updateCartTotal, updateQuantitySafely]);

  const removeProduct = useCallback((productToRemove: ICartProduct) => {
    const updatedProducts = products.filter(
      (product: ICartProduct) => product.id !== productToRemove.id
    );
    setProducts(updatedProducts);
    updateCartTotal(updatedProducts);
  }, [products, setProducts, updateCartTotal]);

  const increaseProductQuantity = useCallback((productToIncrease: ICartProduct) => {
    const updatedProducts = products.map((product: ICartProduct) =>
      updateQuantitySafely(product, productToIncrease, 1)
    );
    setProducts(updatedProducts);
    updateCartTotal(updatedProducts);
  }, [products, setProducts, updateCartTotal, updateQuantitySafely]);

  const decreaseProductQuantity = useCallback((productToDecrease: ICartProduct) => {
    const updatedProducts = products.map((product: ICartProduct) =>
      updateQuantitySafely(product, productToDecrease, -1)
    );
    setProducts(updatedProducts);
    updateCartTotal(updatedProducts);
  }, [products, setProducts, updateCartTotal, updateQuantitySafely]);

  return {
    products,
    addProduct,
    removeProduct,
    increaseProductQuantity,
    decreaseProductQuantity,
  };
};

export default useCartProducts;
