import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Formik, Form, Field, FieldArray, ErrorMessage } from 'formik';
import { toFormikValidationSchema } from 'zod-formik-adapter';
import { expenseFormSchema } from '@/validation/expenseFormSchema';
import { Plus } from 'lucide-react';
import { CircleAlert } from 'lucide-react';
import { Group } from '@/validation/groupSchema';
import { AddExpenseInitialValues } from '@/components/bill-splitting/tabs/ExpensesHistoryTab';

export interface ExpenseShare {
  userId: number;
  included: boolean;
  value?: number;
}

export default function AddExpenseDialog({
  open,
  setOpen,
  trip,
  onSubmit,
  initialValues,
}: {
  open: boolean;
  setOpen: (v: boolean) => void;
  trip: Group;
  onSubmit: (values: AddExpenseInitialValues) => Promise<void>;
  initialValues: AddExpenseInitialValues;
}) {
  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="outline">
          <Plus className="mr-2" />
          Add Expense
        </Button>
      </DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Group Expense</DialogTitle>
        </DialogHeader>
        <Formik
          initialValues={initialValues}
          enableReinitialize
          validationSchema={toFormikValidationSchema(expenseFormSchema)}
          onSubmit={onSubmit}
        >
          {({ values, errors, touched, isSubmitting, setFieldValue }) => (
            <Form className="space-y-4">
              <div className="mt-4">
                <label className="font-semibold" htmlFor="expense-description">
                  Description
                </label>
                <Field
                  id="expense-description"
                  name="description"
                  as={Input}
                  placeholder="Description"
                  disabled={isSubmitting}
                  className={`mt-1 ${
                    errors.description && touched.description
                      ? 'border-red-500'
                      : ''
                  }`}
                />
                <ErrorMessage
                  name="description"
                  render={(msg) => (
                    <div className="flex items-center mt-1 text-sm text-red-500">
                      <CircleAlert className="mr-2" />
                      <p>{msg}</p>
                    </div>
                  )}
                />
              </div>

              <div className="mt-4">
                <label className="font-semibold" htmlFor="expense-total-amount">
                  Total amount
                </label>
                <Field
                  id="expense-total-amount"
                  name="totalAmount"
                  as={Input}
                  type="number"
                  placeholder="Total Amount"
                  disabled={isSubmitting}
                  className={`mt-1 ${
                    errors.totalAmount && touched.totalAmount
                      ? 'border-red-500'
                      : ''
                  }`}
                />
                <ErrorMessage
                  name="totalAmount"
                  render={(msg) => (
                    <div className="flex items-center mt-1 text-sm text-red-500">
                      <CircleAlert className="mr-2" />
                      <p>{msg}</p>
                    </div>
                  )}
                />
              </div>

              <div className="mt-4">
                <label
                  className="font-semibold pr-2"
                  htmlFor="expense-split-method"
                >
                  Split Method
                </label>
                <Field
                  id="expense-split-method"
                  name="splitMethod"
                  as="select"
                  className="border rounded px-2 py-1"
                  disabled={isSubmitting}
                >
                  <option value="equally">Equally</option>
                  <option value="percentage">Percentage</option>
                  <option value="amount">Amount</option>
                  <option value="share">Share</option>
                </Field>
                <ErrorMessage
                  name="splitMethod"
                  render={(msg) => (
                    <div className="flex items-center mt-1 text-sm text-red-500">
                      <CircleAlert className="mr-2" />
                      <p>{msg}</p>
                    </div>
                  )}
                />
              </div>
              <FieldArray name="shares">
                {() => (
                  <div className="space-y-2">
                    {values.shares.map((user: ExpenseShare, idx: number) => (
                      <div
                        key={user.userId}
                        className="flex items-center gap-4"
                      >
                        <div className="flex items-center flex-grow gap-2">
                          <Field
                            type="checkbox"
                            name={`shares.${idx}.included`}
                            checked={user.included}
                            onChange={(
                              e: React.ChangeEvent<HTMLInputElement>,
                            ) =>
                              setFieldValue(
                                `shares.${idx}.included`,
                                e.target.checked,
                              )
                            }
                          />
                          <Label className="w-40">
                            {
                              trip?.memberships.find(
                                (u) =>
                                  (u as { userId: number }).userId ===
                                  user.userId,
                              )?.userEmail
                            }
                          </Label>
                        </div>
                        {(values.splitMethod === 'percentage' ||
                          values.splitMethod === 'amount' ||
                          values.splitMethod === 'share') &&
                          user.included && (
                            <div className="flex flex-col items-end w-40 ml-auto">
                              <div className="flex items-center w-full">
                                <span className="text-xs text-gray-500 mr-2">
                                  {values.splitMethod === 'percentage'
                                    ? '%'
                                    : values.splitMethod === 'amount'
                                      ? 'zł'
                                      : '#'}
                                </span>
                                <Field
                                  name={`shares.${idx}.value`}
                                  as={Input}
                                  type="number"
                                  step={
                                    values.splitMethod === 'share'
                                      ? '1'
                                      : '0.01'
                                  }
                                  min={
                                    values.splitMethod === 'share' ? '1' : '0'
                                  }
                                  placeholder={
                                    values.splitMethod === 'percentage'
                                      ? 'Percent'
                                      : values.splitMethod === 'amount'
                                        ? 'Amount'
                                        : 'Shares'
                                  }
                                  className="w-full"
                                  disabled={!user.included || isSubmitting}
                                  onChange={(
                                    e: React.ChangeEvent<HTMLInputElement>,
                                  ) =>
                                    setFieldValue(
                                      `shares.${idx}.value`,
                                      e.target.value === ''
                                        ? ''
                                        : values.splitMethod === 'share'
                                          ? parseInt(e.target.value, 10)
                                          : parseFloat(e.target.value),
                                    )
                                  }
                                />
                              </div>
                              {Array.isArray(errors.shares) &&
                                errors.shares[idx] &&
                                typeof errors.shares[idx] === 'object' &&
                                errors.shares[idx] !== null &&
                                'value' in errors.shares[idx] &&
                                typeof (
                                  errors.shares[idx] as { value?: string }
                                ).value === 'string' &&
                                (errors.shares[idx] as { value?: string })
                                  .value && (
                                  <div className="flex items-center mt-1 text-xs text-red-500">
                                    <CircleAlert className="mr-1" />
                                    <p>
                                      {
                                        (
                                          errors.shares[idx] as {
                                            value: string;
                                          }
                                        ).value
                                      }
                                    </p>
                                  </div>
                                )}
                            </div>
                          )}
                      </div>
                    ))}
                    {typeof errors.shares === 'string' && (
                      <div className="flex items-center mt-1 text-xs text-red-500">
                        <CircleAlert className="mr-1" />
                        <p>{errors.shares}</p>
                      </div>
                    )}
                  </div>
                )}
              </FieldArray>
              <Button
                type="submit"
                className="w-full bg-primary-500 text-white hover:bg-primary-600"
                disabled={isSubmitting}
              >
                Submit
              </Button>
            </Form>
          )}
        </Formik>
      </DialogContent>
    </Dialog>
  );
}
