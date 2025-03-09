import { Formik, Field, Form, ErrorMessage } from 'formik';
import { z } from 'zod';
import { toFormikValidationSchema } from 'zod-formik-adapter';
import {
  createGroupFormSchema,
  TripFormPropsSchema,
} from '@/validation/groupSchemas';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import { Button } from '@/components/ui/button';
import { CircleAlert, X } from 'lucide-react';
import React from 'react';

const defaultInitialValues: z.infer<typeof createGroupFormSchema> = {
  name: '',
  description: '',
  tripStartDate: new Date(),
  tripEndDate: new Date(),
  tags: [],
  groupImageUrl: '',
};

function TripForm({
  onSubmit,
  initialValues = defaultInitialValues,
  isSubmitting = false,
  error,
  mode = 'create',
}: z.infer<typeof TripFormPropsSchema>) {
  return (
    <Formik
      initialValues={initialValues}
      validationSchema={toFormikValidationSchema(createGroupFormSchema)}
      onSubmit={(values, actions) => {
        onSubmit(values, actions);
      }}
    >
      {({ errors, touched, values, setFieldValue, setFieldError }) => (
        <Form>
          <div className="mt-4">
            <label className="font-semibold" htmlFor="name">
              Name
            </label>
            <Field
              name="name"
              as={Input}
              className={`mt-1 ${
                errors.name && touched.name ? 'border-red-500' : ''
              }`}
            />
            <ErrorMessage
              name="name"
              render={(msg) => (
                <div className="flex items-center mt-1 text-sm text-red-500">
                  <CircleAlert className="mr-2" />
                  <p>{msg}</p>
                </div>
              )}
            />
          </div>

          <div className="mt-4">
            <label className="font-semibold" htmlFor="description">
              Description
            </label>
            <Field
              name="description"
              as={Textarea}
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
            <label className="font-semibold" htmlFor="tripStartDate">
              Start Date
            </label>
            <Field
              name="tripStartDate"
              type="date"
              as={Input}
              className={`mt-1 ${
                errors.tripStartDate && touched.tripStartDate
                  ? 'border-red-500'
                  : ''
              }`}
              value={
                values.tripStartDate instanceof Date &&
                !isNaN(values.tripStartDate.getTime())
                  ? values.tripStartDate.toISOString().split('T')[0]
                  : ''
              }
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                const newDate = new Date(e.target.value);
                setFieldValue('tripStartDate', newDate);
              }}
            />
            <ErrorMessage
              name="tripStartDate"
              render={(msg) => (
                <div className="flex items-center mt-1 text-sm text-red-500">
                  <CircleAlert className="mr-2" />
                  <p>{msg}</p>
                </div>
              )}
            />
          </div>

          <div className="mt-4">
            <label className="font-semibold" htmlFor="tripEndDate">
              End Date
            </label>
            <Field
              name="tripEndDate"
              type="date"
              as={Input}
              className={`mt-1 ${
                errors.tripEndDate && touched.tripEndDate
                  ? 'border-red-500'
                  : ''
              }`}
              value={
                values.tripEndDate instanceof Date &&
                !isNaN(values.tripEndDate.getTime())
                  ? values.tripEndDate.toISOString().split('T')[0]
                  : ''
              }
              onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                const newDate = new Date(e.target.value);
                setFieldValue('tripEndDate', newDate);
              }}
            />
            <ErrorMessage
              name="tripEndDate"
              render={(msg) => (
                <div className="flex items-center mt-1 text-sm text-red-500">
                  <CircleAlert className="mr-2" />
                  <p>{msg}</p>
                </div>
              )}
            />
          </div>

          <div className="mt-4">
            <label className="font-semibold">Tags</label>
            <Input
              placeholder="Add a tag and press Enter"
              onKeyDown={(e: React.KeyboardEvent<HTMLInputElement>) => {
                if (e.key === 'Enter' && e.currentTarget.value.trim() !== '') {
                  e.preventDefault();
                  if (values.tags.length < 10) {
                    setFieldValue('tags', [
                      ...values.tags,
                      e.currentTarget.value.trim(),
                    ]);
                    setFieldError('tags', '');
                  } else {
                    setFieldError('tags', 'You can only add up to 10 tags');
                  }
                  e.currentTarget.value = '';
                }
              }}
            />
            <div className="flex flex-wrap gap-2 mt-2">
              {values.tags.map((tag, index: number) => (
                <div
                  key={index}
                  className="bg-primary-500 px-3 py-1 text-small-medium text-white rounded-lg flex items-center gap-2"
                >
                  <span className="flex-1">#{tag}</span>
                  <button
                    type="button"
                    className="p-2 hover:bg-primary-600 rounded-full transition-colors text-terminate-color hover:text-red-500"
                    onClick={() => {
                      const newTags = values.tags.filter((_, i) => i !== index);
                      setFieldValue('tags', newTags);
                    }}
                  >
                    <X size={16} className="" />
                  </button>
                </div>
              ))}
            </div>
            <ErrorMessage
              name="tags"
              render={(msg) => (
                <div className="flex items-center mt-1 text-sm text-red-500">
                  <CircleAlert className="mr-2" />
                  <p>{msg}</p>
                </div>
              )}
            />
          </div>

          <div className="mt-4">
            <label className="font-semibold" htmlFor="groupImageUrl">
              Trip Image URL
            </label>
            <Field
              name="groupImageUrl"
              type="url"
              as={Input}
              className={`mt-1 ${
                errors.groupImageUrl && touched.groupImageUrl
                  ? 'border-red-500'
                  : ''
              }`}
            />
            <ErrorMessage
              name="groupImageUrl"
              render={(msg) => (
                <div className="flex items-center mt-1 text-sm text-red-500">
                  <CircleAlert className="mr-2" />
                  <p>{msg}</p>
                </div>
              )}
            />
          </div>

          <Button
            className="bg-primary-600 hover:bg-primary-500 p-6 mt-4"
            type="submit"
            disabled={isSubmitting}
          >
            {isSubmitting
              ? 'Submitting...'
              : mode === 'create'
                ? 'Create Trip'
                : 'Save Changes'}
          </Button>

          {error && (
            <div className="mt-4 text-sm text-red-500">
              <CircleAlert className="inline mr-2" />
              {error}
            </div>
          )}
        </Form>
      )}
    </Formik>
  );
}

export default TripForm;
