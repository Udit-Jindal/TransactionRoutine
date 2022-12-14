package com.pismo.txnroutine.model;

import static com.pismo.txnroutine.exception.CustomMessages.UNKNOWN_OPERATION_TYPE;
import com.pismo.txnroutine.exception.InvalidOperationException;

public enum Operation
{
    NORMAL_PURCHASE(1),
    INSTALLMENT_PURCHASE(2),
    WITHDRAWAL(3),
    CREDIT_VOUCHER(4);

    final Integer value;

    Operation(Integer value)
    {
        this.value = value;
    }

    public Integer getValue()
    {
        return value;
    }

    public static Operation parse(int value) throws InvalidOperationException
    {
        switch (value)
        {
            case 1:
                return NORMAL_PURCHASE;
            case 2:
                return INSTALLMENT_PURCHASE;
            case 3:
                return WITHDRAWAL;
            case 4:
                return CREDIT_VOUCHER;
            default:
                throw new InvalidOperationException(UNKNOWN_OPERATION_TYPE
                        + " : " + value);
        }
    }
}
